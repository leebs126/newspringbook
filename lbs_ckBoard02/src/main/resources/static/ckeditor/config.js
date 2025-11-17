/**
 * @license Copyright (c) 2003-2023, CKSource Holding sp. z o.o. All rights reserved.
 * For licensing, see https://ckeditor.com/legal/ckeditor-oss-license
 */

CKEDITOR.editorConfig = function( config ) {
	// Define changes to default configuration here. For example:
	// config.language = 'fr';
	// config.uiColor = '#AADC6E';
};
/*
config.toolbar = [
		{ name: 'document', items: [ 'Source', 'NewPage', '-','ExportPdf','-', 'Preview', 'Print',  'Templates' ] },
		'/',
		{ name: 'clipboard', items: [ 'Cut', 'Copy', 'Paste', 'PasteText', '-', 'Undo', 'Redo' ] },
		{ name: 'editing', items: [ 'Find', 'Replace' ] },
		{ name: 'links', items: [ 'Link', 'Unlink' ] },
		'/',
		{ name: 'insert', items: [ 'Image', 'Table', 'HorizontalRule', 'SpecialChar' ] },
		{ name: 'tools', items: [ 'Maximize' ] },
		{ name: 'about', items: [ 'About' ] },
        '/',
		{ name: 'styles', items: [ 'Font', 'FontSize' ] },
		{ name: 'colors', items: [ 'TextColor', 'BGColor' ] },
		{ name: 'basicstyles', items: [ 'Bold', 'Italic', 'Underline', 'Strike' ] },
		{ name: 'paragraph', items: [ 'NumberedList', 'BulletedList', '-', 'Outdent', 'Indent', '-', 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock' ] }
	];
*/	
/*
CKEDITOR.on('dialogDefinition', function (ev) {
    var dialogName = ev.data.name;
    var dialogDefinition = ev.data.definition;

    if (dialogName == 'image') {
        dialogDefinition.onShow = function () {
            var uploadTab = this.definition.getContents('Upload');
            var uploadButton = uploadTab.get('uploadButton');

            uploadButton['filebrowser']['onError'] = function (errorCode, xhr) {
                // 에러 코드 500을 무시하려면 아래 조건을 추가하세요.
                if (errorCode === 500) {
                    alert("500 에러")
					return false;
                }

                // 기본 오류 처리를 사용하려면 아래 코드를 주석 해제하세요.
                // return true;
            };
        };
    }
});
*/