const editor = new toastui.Editor({
    el: document.querySelector('#editor'),
    height: '500px',
    initialEditType: 'wysiwyg',
    previewStyle: 'vertical',
    initialValue: document.getElementById('content-hidden').value || '',
    hooks: {
        addImageBlobHook: async (blob, callback) => {
            const reader = new FileReader();
            reader.onload = (e) => callback(e.target.result, '이미지');
            reader.readAsDataURL(blob);
        }
    }
});

window.getEditorContent = () => editor.getHTML();