var express = require('express');
var route = express.Router();

const map = {
    'dollar-vietnam': 22000
}

function convert(src, des, val) {
    var ratio = map[src + '-' + des];
    if (ratio == undefined) {
        ratio = (1 / map[des + '-' + src]);
    }

    return ratio == undefined ? -1 : ratio * val;

}


route.get('/convert', function (req, res) {

    var src = req.query['src'];
    var des = req.query['des'];
    var val = req.query['val'];
    var result = convert(src, des, val);
    if (result == -1) {
        res.json({
            success: false, 'error': 'Cannot convert', 'Supported': ['dollar-vietnam', 'vietnam-dollar']
        });
    } else {
        res.json({success: true, 'result': result});
    }

})
module.exports = route;