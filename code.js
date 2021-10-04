const net = require('net');
const host = "homework.islab.work";

var port = 8000;

if (process.argv.length > 3) {
    port = parseInt(process.argv[2]);
}