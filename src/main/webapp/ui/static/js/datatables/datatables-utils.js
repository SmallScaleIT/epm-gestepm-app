let dTable;
let customDataTable;

class CustomDataTable {

    constructor(columns, endpoint, data, actions, expand, filters, columnDefs, stickyColumns, jsAction) {
        this.columns = columns
        this.endpoint = endpoint
        this.data = data;
        this.actions = actions
        this.expand = expand
        this.filters = filters
        this.columnDefs = columnDefs
        this.stickyColumns = stickyColumns
        this.jsAction = jsAction
        this.currentTable = null;
    }

    columns() {
        return this.columns
    }

    endpoint() {
        return this.endpoint
    }

    actions() {
        return this.actions
    }

    expand() {
        return this.expand
    }

    filters() {
        return this.filters
    }

    setFilters(filters) {
        this.filters = filters
    }

    columnDefs() {
        return this.columnDefs
    }

    setColumnDefs(columnDefs) {
        this.columnDefs = columnDefs
    }

    stickyColumns() {
        return this.stickyColumns
    }

    jsAction() {
        return this.jsAction
    }

    setJsAction(jsAction) {
        this.jsAction = jsAction
    }

    currentTable() {
        return this.currentTable;
    }

    setCurrentTable(currentTable) {
        this.currentTable = currentTable
    }
}

function createSimpleDataTable(tableId, customDataTable, locale) {
    return $(tableId).DataTable({
        lengthChange: false,
        searching: false,
        processing: true,
        data: customDataTable.data,
        columns: parseArrayToDataTableColumns(customDataTable.columns),
        columnDefs: generateColumnDefs(customDataTable),
        rowId: customDataTable.columns[0],
        language: {
            url: '/ui/static/lang/datatables/' + locale + '.json'
        },
        fixedColumns: customDataTable.stickyColumns,
        dom: '<\'top\'i>rt<\'bottom\'p><\'clear\'>',
    })
}

function createDataTable(tableId, customDataTable, locale) {
    return $(tableId).DataTable({
        lengthChange: false,
        searching: false,
        processing: true,
        serverSide: customDataTable.endpoint,
        order: generateDefaultOrderTable(customDataTable),
        ajax: {
            url: customDataTable.endpoint,
            // beforeSend: function (request) {
            //     let token = getToken()
            //     request.setRequestHeader('Authorization', 'Bearer ' + token)
            // },
            data: function (d) {
                calculateQueryParams(d, customDataTable)
            },
            dataSrc: function (res) {

                res.recordsTotal = res.metadata.pagination.itemsTotal
                res.recordsFiltered = res.metadata.pagination.itemsTotal

                return res.data
            }
        },
        columns: parseArrayToDataTableColumns(customDataTable.columns),
        columnDefs: generateColumnDefs(customDataTable),
        rowId: customDataTable.columns[0],
        language: {
            url: '/ui/static/lang/datatables/' + locale + '.json'
        },
        fixedColumns: customDataTable.stickyColumns,
        dom: '<\'top\'i>rt<\'bottom\'p><\'clear\'>',
        drawCallback: function (settings) {
            generateQueryParams(settings);
        },
    })
}

function generateDefaultOrderTable(customDataTable) {
    const params = new URLSearchParams(window.location.search);
    const orderBy = params.get('orderBy');
    const order = params.get('order') || 'desc';

    let orderIndex = 0;
    if (orderBy && Array.isArray(customDataTable.columns)) {
        const idx = customDataTable.columns.indexOf(orderBy);
        if (idx !== -1) orderIndex = idx;
    }

    return orderIndex ? [[orderIndex, order]] : [[0]];
}

function generateQueryParams(settings) {

    let staticParams = ['offset', 'limit', '_expand'];
    let data = settings.oAjaxData;
    let ajaxData = {...data};
    let pageNumber = ajaxData.offset / ajaxData.limit + 1;
    if (pageNumber > 1) {
        ajaxData.pageNumber = pageNumber;
    } else {
        delete ajaxData.pageNumber;
    }
    if (!ajaxData.order) {
        delete ajaxData.orderBy;
    }

    let keys = Object.keys(ajaxData).filter(field => !staticParams.includes(field) && ajaxData[field] !== undefined);
    let queryParams = keys.map(key => {
        return key + '=' + ajaxData[key];
    }).join('&');

    let currentURL = new URL(window.location.href);
    currentURL.search = '';

    window.history.pushState(null, '', queryParams ? '?' + queryParams : currentURL.toString());
}

function generateColumnDefs(customDataTable) {

    let columnDefs = [];

    columnDefs.push(
        {
            targets: '_all',
            className: 'text-center',
            defaultContent: '-'
        },
        {
            targets: -1,
            orderable: false,
            render: function (data, type, row) {
                return printActions(row, customDataTable.actions, row.id, customDataTable.jsAction)
            }
        }
    )

    if (customDataTable.columnDefs) {
        columnDefs.push(...customDataTable.columnDefs)
    }

    return columnDefs;
}

function deleteDataTablesQueryParams(d) {

    delete d.columns
    delete d.draw
    delete d.length
    delete d.order
    delete d.search
    delete d.start
}

function calculateQueryParams(d, customDataTable) {

    let offset = d.start
    let limit = d.length

    deleteDataTablesQueryParams(d)

    d.offset = offset
    d.limit = limit

    calculateFiltersQueryParams(d, customDataTable)
    calculateOrderQueryParams(d, customDataTable)
    calculateExpandQueryParams(d, customDataTable)
}

function calculateFiltersQueryParams(d, customDataTable) {

    let filters = customDataTable.filters;

    if (filters) {

        filters.forEach(f => {

            if (f) {
                Object.assign(d, f);
            }
        });
    }
}

function calculateOrderQueryParams(d, customDataTable) {

    let info = customDataTable.currentTable.order()
    let orderIndex = info[0][0]

    d.order = info[0][1]
    d.orderBy = customDataTable.columns[orderIndex]
}

function calculateExpandQueryParams(d, customDataTable) {

    if (customDataTable.expand) {
        d._expand = customDataTable.expand.join(',')
    }
}

function parseArrayToDataTableColumns(columns) {

    let dtColumns = []

    columns.forEach(c => {
        let columnObject = {data: c}
        dtColumns.push(columnObject)
    });

    return dtColumns
}

function printActions(data, actions, id, jsAction) {

    let buttonHtml = '';

    actions.forEach(a => {

        let currentUrl = a.url;

        if (currentUrl && data.detailUrl) {
            currentUrl = currentUrl.replace('{detailUrl}', data.detailUrl);
        }

        if (a.action === 'view') { // FIXME: && a.permission && authentication.permissions.includes(a.permission)) {
            if (a.url ) {
                buttonHtml += '<a href="' + currentUrl.replace('{id}', id) + '" class="menu-link">' +
                    '<em class="far fa-eye"></em>' +
                    '</a>';
            } else {
                buttonHtml += '<em class="fas fa-eye" onclick="view(' + id + ')"></em>';
            }
        }

        if (a.action === 'validate') {
            if (checkConditionGroups(data, a.conditionGroups)) {
                buttonHtml += '<em class="far fa-thumbs-up" onclick="validate(' + id + ')"></em>';
            }
        }

        if (a.action === 'decline') {
            if (checkConditionGroups(data, a.conditionGroups)) {
                buttonHtml += '<em class="far fa-thumbs-down" onclick="decline(' + id + ')"></em>';
            }
        }

        if (a.action === 'edit') { // FIXME: } && a.permission && authentication.permissions.includes(a.permission)) {
            if (a.type === 'redirect') {
                buttonHtml += '<a href="' + currentUrl.replace('{id}', id) + '" class="menu-link">' +
                    '<em class="fas fa-edit"></em>' +
                    '</a>';
            } else {
                buttonHtml += '<em class="fas fa-edit" onclick="edit(' + id + ')"></em>';
            }
        }

        if (a.action === 'delete') { // FIXME: } && a.permission && authentication.permissions.includes(a.permission)) {
            if (checkConditionGroups(data, a.conditionGroups)) {
                const onclick = jsAction ? `onclick="remove${jsAction}(${id})"` : `onclick="remove(${id})"`;
                buttonHtml += '<em class="far fa-trash-alt" ' + onclick + '></em>';
            }
        }

        if (a.action === 'file') {
            if (checkConditionGroups(data, a.conditionGroups)) {
                buttonHtml += '<a href="' + currentUrl.replace('{id}', id) + '" target="_blank" class="menu-link">' +
                    '<em class="fas fa-file-alt"></em>' +
                    '</a>';
            }
        }

        if (a.action === 'file-pdf') {
            if (checkConditionGroups(data, a.conditionGroups)) {
                buttonHtml += '<a href="' + currentUrl.replace('{id}', id) + '" target="_blank" class="menu-link">' +
                    '<em class="fas fa-file-pdf"></em>' +
                    '</a>';
            }
        }
    });

    return buttonHtml;
}

function checkConditionGroups(data, conditionGroups) {
    if (conditionGroups) {
        return conditionGroups.some(group => {
            return group.conditions.every(condition => {
                if (condition.key === 'status') {
                    if (condition.operation === '!==') {
                        return !condition.value.includes(data.status);
                    } else if (condition.operation === '===') {
                        return condition.value.includes(data.status);
                    }
                } else if (condition.key === 'roleId') {
                    if (condition.operation === '>=') {
                        if (condition.value.includes('ROLE_PL_ID')) {
                            return condition.current >= 4;
                        } else if (condition.value.includes('ROLE_ADMINISTRATION_ID')) {
                            return condition.current >= 7;
                        }
                    }
                } else if (condition.key === 'endDate') {
                    if (condition.operation === '!==') {
                        return condition.value[0] !== data.endDate;
                    }
                } else if (condition.key === 'shareType') {
                    if (condition.operation === '===') {
                        return condition.value.includes(data.type);
                    }
                } else if (condition.key === 'action') {
                    if (condition.operation === '===') {
                        return condition.value.includes(data.action);
                    }
                }
                return false;
            });
        });
    }

    return true;
}
