package com.epm.gestepm.lib.logging.aspect;

import static com.epm.gestepm.lib.logging.aspect.LogExecutionAspectHelper.shortOutputData;
import static com.epm.gestepm.lib.logging.aspect.LogExecutionAspectUtils.isVoid;
import static com.epm.gestepm.lib.logging.constants.LogDataKeys.LOG_CODE_TRACE_METHOD_CACHE_INFO_IS_CACHED;
import static com.epm.gestepm.lib.logging.constants.LogDataKeys.LOG_CODE_TRACE_METHOD_CACHE_INFO_IS_EVICT;
import static com.epm.gestepm.lib.logging.constants.LogDataKeys.LOG_CODE_TRACE_OUTPUT;
import static com.epm.gestepm.lib.logging.constants.LogLayerMarkers.*;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import com.epm.gestepm.lib.logging.AppLogger;
import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.lib.logging.annotation.LogExecution;
import com.epm.gestepm.lib.logging.applog.LogHandler;
import com.epm.gestepm.lib.logging.inputparser.LogInputParser;
import com.epm.gestepm.lib.logging.outputparser.LogOutputParser;
import com.epm.gestepm.lib.user.UserProvider;
import com.epm.gestepm.lib.user.data.UserData;
import com.epm.gestepm.lib.user.data.UserLogin;
import com.epm.gestepm.lib.utils.TimeFormatUtils;
import ch.qos.logback.classic.Level;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Order(100)
@Component
@RequiredArgsConstructor
public class LogExecutionAspect {

    private static final Set<String> INFO_LOG_LAYERS = Set.of(REST, VIEW, PROCESS_SERVICE);

    private final ApplicationContext applicationContext;

    private final UserProvider userProvider;

    @Around(value = "@within(parent) && @annotation(execution)", argNames = "joinPoint,parent,execution")
    public Object logExecution(final ProceedingJoinPoint joinPoint, final EnableExecutionLog parent,
            final LogExecution execution) throws Throwable {

        final Class<?> targetClass = LogExecutionAspectUtils.getTargetClass(joinPoint);

        final LogHandler logInfoHandler = AppLogger.forClass(targetClass).handler();
        final LogHandler logDebugHandler = AppLogger.forClass(targetClass).handler();

        final Optional<UserLogin> userLogin = this.userProvider.get(UserLogin.class);
        final String userId = String.valueOf(userLogin.map(UserData::getValue).orElse(null));

        final Map<String, Object> userInfo = Map.of("userId", userId);
        final Map<String, Object> permissions = LogExecutionAspectHelper.extractPermissionInfo(joinPoint);
        final Map<String, Object> codeMarkers = LogExecutionAspectHelper.extractCodeMarkers(joinPoint);
        final Map<String, Object> cacheInfo = LogExecutionAspectHelper.extractCacheInfo(joinPoint, applicationContext);

        final boolean isCached = (boolean) cacheInfo.getOrDefault(LOG_CODE_TRACE_METHOD_CACHE_INFO_IS_CACHED, false);
        final boolean isEvict = (boolean) cacheInfo.getOrDefault(LOG_CODE_TRACE_METHOD_CACHE_INFO_IS_EVICT, false);

        final boolean enableInfo = isInfoLogEnabledForLayer(parent.layerMarker());

        logInfoHandler
            .layer(parent.layerMarker())
            .operation(execution.operation())
            .data(cacheInfo)
            .data(codeMarkers)
            .data(permissions)
            .data(userInfo);

        logDebugHandler
            .layer(parent.layerMarker())
            .operation(execution.operation())
            .data(cacheInfo)
            .data(codeMarkers)
            .data(permissions)
            .data(userInfo);

        if (execution.logIn()) {

            final Class<? extends LogInputParser> inputParser = execution.inputParser();
            final Map<String, Object> inputData = LogExecutionAspectHelper.extractInput(inputParser, joinPoint);

            String logMsg = execution.msgIn();
            logMsg = logMsg.isBlank() ? execution.msg() : logMsg;
            logMsg = logMsg.isBlank() ? String.format("Entering %s", parent.layerMarker()) : logMsg;
            logMsg = isCached ? "[[!] Might put in cache] - ".concat(logMsg) : logMsg;
            logMsg = isEvict ? "[[!] Might evict cache] - ".concat(logMsg) : logMsg;

            if (enableInfo) {
                logInfoHandler.flowIn()
                        .msg(logMsg)
                        .data(inputData)
                        .logAs(execution.level());
            }

            logDebugHandler.data(inputData);
        }

        final long start = System.nanoTime();
        final Object methodReturnValue;

        try {

            methodReturnValue = joinPoint.proceed();

        } catch (final Exception exception) {

            final long timeUntilException = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
            final String executionTime = TimeFormatUtils.asHumanReadable(timeUntilException);

            String logMsg = execution.errorMsg();
            logMsg = logMsg.isBlank() ? execution.msg() : logMsg;
            logMsg = logMsg.isBlank() ? String.format("Error in %s", parent.layerMarker()) : logMsg;

            logInfoHandler.flowErr()
                    .msg(logMsg)
                    .execTime(executionTime)
                    .exceptionClass(exception.getClass().getCanonicalName())
                    .exceptionMsg(exception.getMessage())
                    .error();

            throw exception;
        }

        final long execTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
        final String executionTime = TimeFormatUtils.asHumanReadable(execTime);

        if (execution.logOut()) {

            final String shortOutputData = isVoid(joinPoint) ? ", result <void>" : shortOutputData(methodReturnValue, execution.displayEnabled());

            String logMsg = execution.msgOut();
            logMsg = logMsg.isBlank() ? execution.msg() : logMsg;
            logMsg = logMsg.isBlank() ? String.format("Exiting %s", parent.layerMarker()) : logMsg;
            logMsg = logMsg.concat(shortOutputData).trim();
            logMsg = isCached ? "[[!] Might put in cache] - ".concat(logMsg) : logMsg;
            logMsg = isEvict ? "[[!] Might evict cache] - ".concat(logMsg) : logMsg;

            if (enableInfo) {
                logInfoHandler.flowOut()
                        .msg(logMsg)
                        .execTime(executionTime)
                        .data(LOG_CODE_TRACE_OUTPUT, shortOutputData)
                        .logAs(execution.level());
            }

            if (execution.debugOut() && logDebugHandler.isLevelActive(Level.DEBUG)) {

                final Class<? extends LogOutputParser> outputParser = execution.outputParser();
                final Map<String, Object> outputData = LogExecutionAspectHelper.parseOutput(outputParser,
                        methodReturnValue);

                logDebugHandler.flowOut()
                    .msg(logMsg)
                    .execTime(executionTime)
                    .data(outputData)
                    .debug();
            }
        }

        return methodReturnValue;
    }

    private boolean isInfoLogEnabledForLayer(final String layerMarker) {
        return INFO_LOG_LAYERS.contains(layerMarker.toLowerCase());
    }

}
