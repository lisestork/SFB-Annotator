
var app = {
	name: 'Semantic Field Book Annotator',

	items: {},

	init: function(){
		this.getCollections();
		Navigation.init();
	},

	getCollections: function(){
		require(['json!data/data.json'], function(data){
			this.items = data.items;
			itemViewer.item = data.items; //put data in itemViewer
			itemViewer.depth = (app.checkDepth(eval(data)))/2;
			itemViewer.init();
			breadCrumbs.init();
			imageViewer.init();
			pagination.init();
			accounts.init();
			semanticAnnotation.init();
		});
	},

	checkDepth: function(object){
	  var level = 1;
	  var key;
    for (key in object){
	    if (!object.hasOwnProperty(key))
				continue;
	    if (typeof object[key] == 'object'){
				var depth = this.checkDepth(object[key]) + 1;
				level = Math.max(depth, level);
	    }
		}
		return level;
	}
}
