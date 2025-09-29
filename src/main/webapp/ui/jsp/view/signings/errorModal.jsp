<div id="${param.id}" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <div class="modal-title">
                    <h5>${param.modalTitle}</h5>
                </div>
            </div>

            <div class="modal-body"></div>

            <div class="modal-footer clearfix">
                <div class="w-100">
                    <div class="float-left">
                        <button type="button" class="btn btn-sm" data-dismiss="modal">
                            ${param.closeButton}
                        </button>
                    </div>
                    <div class="float-right">
                        <a class="btn btn-sm btn-primary" data-modal-id="modal-href">${param.hrefTitle}</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>