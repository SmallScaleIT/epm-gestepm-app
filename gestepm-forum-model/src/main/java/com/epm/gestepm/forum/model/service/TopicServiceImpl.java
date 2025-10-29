package com.epm.gestepm.forum.model.service;

import com.epm.gestepm.forum.model.api.dto.Attachment;
import com.epm.gestepm.forum.model.api.dto.Post;
import com.epm.gestepm.forum.model.api.dto.Topic;
import com.epm.gestepm.forum.model.api.service.TopicService;
import com.epm.gestepm.forum.model.dao.AttachmentRepository;
import com.epm.gestepm.forum.model.dao.PostRepository;
import com.epm.gestepm.forum.model.dao.TopicRepository;
import com.epm.gestepm.forum.model.dao.UserForumRepository;
import com.epm.gestepm.lib.file.FileUtils;
import com.epm.gestepm.lib.ftp.SFTPClient;
import com.mysql.jdbc.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLConnection;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional("forumTransactionManager")
public class TopicServiceImpl implements TopicService {

	private static final Log log = LogFactory.getLog(TopicServiceImpl.class);
	
	@Value("${gestepm.forum.ftp.server}")
	private String forumFtpServer;
	
	@Value("${gestepm.forum.ftp.port}")
	private int forumFtpPort;
	
	@Value("${gestepm.forum.ftp.user}")
	private String forumFtpUser;
	
	@Value("${gestepm.forum.ftp.password}")
	private String forumFtpPassword;
	
	@Autowired
	private AttachmentRepository attachmentRepository;
	
	@Autowired
	private PostRepository postRepository;
	
	@Autowired
	private TopicRepository topicRepository;
	
	@Autowired
	private UserForumRepository userForumRepository;

	@Async
	@Override
	public CompletableFuture<Topic> create(String topicTitle, String topicContent, Integer forumId, String userIp, String username, List<MultipartFile> files) {

		long topicTimer = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
		int hasAttachments = CollectionUtils.isNotEmpty(files) ? 1 : 0;
		
		Long userForumId = userForumRepository.findIdByUsername(username);
		
		if (userForumId == null) {
			log.error("El usuario " + username + " no existe en el foro.");
			return CompletableFuture.completedFuture(null);
		}
		
		Topic topic = null;
		
		if (topicTitle.startsWith("Re:")) {
			topic = topicRepository.findById(forumId.longValue()).orElse(null);
			
			if (topic == null) {
				return CompletableFuture.completedFuture(null);
			}
			
			topic.setTopicLastPosterName(username);
			topic.setTopicLastPostSubject(topicTitle);
			topic.setTopicLastPostTime(topicTimer);
			topic.setTopicPostsApproved(topic.getTopicPostsApproved() + 1);
			
		} else {
		
			// Creating Topic Model
			topic = new Topic();
			topic.setForumId(forumId); // Custom ID
			topic.setIconId(0);
			topic.setTopicAttachment(hasAttachments); // 0 = no files, 1 = with files
			topic.setTopicReported(0);
			topic.setTopicTitle(topicTitle);
			topic.setTopicPoster(userForumId);
			topic.setTopicTime(topicTimer);
			topic.setTopicTimeLimit(0);
			topic.setTopicViews(0);
			topic.setTopicStatus(0);
			topic.setTopicFirstPostId(0); // topicFirstPostId
			topic.setTopicFirstPosterName(username);
			topic.setTopicFirstPosterColour("AA0000");
			topic.setTopicLastPosterId(0); // topicFirstPostId
			topic.setTopicLastPosterName(username);
			topic.setTopicLastPosterColour("AA0000");
			topic.setTopicLastPostSubject(topicTitle);
			topic.setTopicLastPostTime(topicTimer);
			topic.setTopicLastViewTime(topicTimer);
			topic.setTopicMovedId(0);
			topic.setTopicBumped(0);
			topic.setTopicBumper(0);
			topic.setPollTitle("");
			topic.setPollStart(0);
			topic.setPollLength(0);
			topic.setPollMaxOptions(1);
			topic.setPollLastVote(0);
			topic.setPollVoteChange(0);
			topic.setTopicVisibility(1);
			topic.setTopicDeleteTime(0);
			topic.setTopicDeleteReason("");
			topic.setTopicDeleteUser(0);
			topic.setTopicPostsApproved(1);
			topic.setTopicPostsUnapproved(0);
			topic.setTopicPostsSoftdeleted(0);
			
			// Insert topic into DB
			topic = topicRepository.save(topic);
		}
		
		// Creating Post Model
		Post post = new Post();
		post.setTopicId(topic.getId());
		post.setForumId(topic.getForumId());
		post.setPosterId(userForumId);
		post.setIconId(0);
		post.setPosterIp(userIp);
		post.setPostTime(topicTimer);
		post.setPostReported(0);
		post.setEnableBbcode(1);
		post.setEnableSmilies(1);
		post.setEnableMagicUrl(1);
		post.setEnableSig(1);
		post.setPostUsername("");
		post.setPostSubject(topicTitle);
		post.setPostText(topicContent);
		post.setPostChecksum(""); // ?? WTF
		post.setPostAttachment(hasAttachments);
		post.setBbcodeBitfield("");
		post.setBbcodeUid(""); // ?? WTF
		post.setPostPostCount(1);
		post.setPostEditTime(0);
		post.setPostEditReason("");
		post.setPostEditUser(0);
		post.setPostEditCount(0);
		post.setPostEditLocked(0);
		post.setPostVisibility(1);
		post.setPostDeleteTime(0);
		post.setPostDeleteReason("");
		post.setPostDeleteUser(0);
		
		// Insert post into DB
		post = postRepository.save(post);
		
		// Update Topic when Post is created
		if (!topicTitle.startsWith("Re:")) {
			topic.setTopicFirstPostId(post.getId());
		}
		
		topic.setTopicLastPostId(post.getId());
		topic = topicRepository.save(topic);
				
		// Adding attachments
		
		if (hasAttachments == 1) {
			
			try {
				
				// Open connection to SFTP
				SFTPClient sftpClient = new SFTPClient(forumFtpServer, forumFtpPort, forumFtpUser, forumFtpPassword);
				sftpClient.open();
				
				for (MultipartFile file : files) {
					
					if (file == null || file.isEmpty()) {
						continue;
					}
					
					String originalFilename = file.getOriginalFilename();
					String fileName = FilenameUtils.removeExtension(file.getOriginalFilename());
					String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();
					
					byte[] fileCompressed = FileUtils.compressImage(file);
					InputStream fileStreamCompressed = new ByteArrayInputStream(fileCompressed);
					
					// Upload file to SFTP
					sftpClient.uploadFile(fileStreamCompressed, "files/" + fileName);
					
					// Mime type
				    String mimeType = URLConnection.guessContentTypeFromName(originalFilename);
				    
				    if (StringUtils.isNullOrEmpty(mimeType)) {
				    	mimeType = "null";
				    }
					
					// Creating Attachment
					Attachment attachment = new Attachment();
					attachment.setPostMsgId(post.getId());
					attachment.setTopicId(topic.getId());
					attachment.setInMessage(0);
					attachment.setPosterId(userForumId);
					attachment.setIsOrphan(0);
					attachment.setPhysicalFilename(fileName);
					attachment.setRealFilename(originalFilename);
					attachment.setDownloadCount(1);
					attachment.setAttachComment("");
					attachment.setExtension(fileExtension);
					attachment.setMimetype(mimeType);
					attachment.setFilesize(fileCompressed.length);
					attachment.setFiletime(topicTimer);
					attachment.setThumbnail(0);
					
					// Saving Attachment
					attachmentRepository.save(attachment);
				}
				
				
			} catch (Exception e) {
				log.error(e);
			}
		}

		// Update Forum Statistics
		topicRepository.updateForumStatsWhenCreateTopic(topic.getForumId(), post.getId(), userForumId, topicTitle, topicTimer, username);
		
		// Update User Statistics
		userForumRepository.updateUserPostStats(userForumId, topicTimer);
		
		return CompletableFuture.completedFuture(topic);
	}
}
