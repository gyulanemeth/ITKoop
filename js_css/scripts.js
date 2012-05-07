window.onload = function () {
    document.body.onresize = function () {
       var canvasNode = document.getElementById('canvas');
       canvasNode.width = canvasNode.parentNode.clientWidth;
       canvasNode.height = canvasNode.parentNode.clientHeight;
    }
    document.body.onresize();
};
