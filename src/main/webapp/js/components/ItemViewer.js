
var itemViewer = {

		item: {},
		path: 'itemViewer',
		depth: 0,

		init: function(){
			this.cacheDom();
			this.bindEvents();
			this.render();
		},

		cacheDom: function(){
			this.$el = $('#folderViewer');
			this.template = this.$el.find('#folder-template').html();
		},

		bindEvents: function(){
			this.$el.delegate('a.thumbnail', 'click', this.selectedItem.bind(this));
			var that = this;
			events.on('breadCrumbSelected', function(diff) {
				that.path = that.path.slice(0, -(diff*8)); 
				that.pathChanged();
			});

			events.on('navbarSelection', function(target){
				if(target == "collections"){
					this.path = 'itemViewer';
					this.pathChanged();
					this.render();
				}
			}.bind(this));
		},

		render: function() {	
			//update folders
			var data = {
					collectionDetails: []			
			};
			eval(this.path+'.item').forEach(function(element) {
				data.collectionDetails.push(							
							{ 
								name: element.name,
								url: element.thumbnail,
								items: element.item.length,
								id: element.id
							}	
				)
			});
			this.$el.html(Mustache.render(this.template, data)); //render template	
		},

		selectedItem: function(event) {
			//search for selected item
			var $selected = $(event.target).closest('a'); //search for clicked item
			var i = this.$el.find('a').index($selected); //search for index clicked item
			var id = this.$el.find("a:eq("+i+")").find('b').html(); //search for id within clicked item
			//added a selection
			events.emit('itemSelected', id);
			//change path
			this.path = this.path+'.item['+i+']'; //object path selected folders
			this.pathChanged();
		},

		pathChanged: function(){
			//change depth
			var oldDepth = this.depth; 
			this.depth = (app.checkDepth(eval(this.path)))/2; //check in which level of the coll we are
			events.emit('depthChanged', {depth:this.depth, oldDepth: oldDepth});
			this.hideOrShowItemviewer(this.depth, oldDepth);
		},

		hideOrShowItemviewer: function(depth, oldDepth){
			//if imagelayer, do not render new collection thumbnails. Instead, push images and show imageviewer.
			if(depth > 1 && oldDepth == 1) { //if we are not yet on an image level. 
				this.$el.show();
				this.render();	//render folders
			} else if(depth > 1 && oldDepth != 1) {
				this.render();	//render folders
			} else {
				this.$el.hide();
			}
		}
	}







