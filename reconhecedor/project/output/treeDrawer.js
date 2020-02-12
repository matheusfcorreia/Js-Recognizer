const { drawTree } = require('tree-image-drawer');
const fs = require('fs');
 
const generateTree = exp => {
  const s = { children: [exp], display:'S' };
 
  drawTree([s], './tree.png');
};
 
 
fs.readFile(__dirname + '/tree.json', 'utf8', (err, data) => {
   generateTree(JSON.parse(data));
});