var imageViewer = {

	imageArray: [],
	selected: [],
	//pagenumber: 1,
	pageID: "",
	stopload: false,

	init: function(){
		this.cacheDom();
		this.createOpenSeadragon();
		this.bindEvents();
	},

	cacheDom: function(){
		this.$el = $('#image');
		this.$loader = $('#loading');
	},

	bindEvents: function(){
		events.on('depthChanged', this.hideOrShowImageviewer.bind(this));
		events.on('pagenumberChanged', function(data){
			this.pageID = data.pageID;
			this.render(data.page);
		}.bind(this));
		this.$el.delegate('#annotate', 'click', function(){
			if(accounts.person[0] == undefined){
				alert("not registered yet");
			} else {
				anno.activateSelector();
			}
		});
		this.viewer.addHandler('open', function(){
			events.emit('viewerOpened',this.pageID);
		}.bind(this));
	},

	hideOrShowImageviewer: function(data){
		if(data.depth == 1){
			this.$el.show();
			this.setFolder(eval(itemViewer.path+'.item'), breadCrumbs.selected);
		} else if (data.depth != 1 && data.oldDepth == 1){
			this.$el.hide();
			//this.stopload = true;
			this.viewer.tileSources = new Array();
			this.viewer.open(this.viewer.tileSources,0);
		};
	},

	setFolder: function(array, path){
		//set everything up for the first image of the folder
		this.imageArray = array;
		pagination.imageArray = array;
		this.selected = path;

		//emit new folder for annotation linking
		//annotationViewer.loadAnnotations();
		events.emit('folderSelected', this.selected[this.selected.length-1]);
		//this.$loader.show();
		//this.stopload = false;
		var that = this;
		var image = new Array();
		for (var i = 0; i < this.imageArray.length; i++){
			//if (that.stopload == true) { break; alert('hi')};
			image[i] = new Image();
			image[i].onload = (function(nr){
				return function () {
					that.viewer.tileSources[nr] =
					{
						type: 'legacy-image-pyramid',
						id: image[nr].url,
						crossOriginPolicy: 'Anonymous',
					    ajaxWithCredentials: false,
			            levels:[{
			                url: image[nr].url,
			                height: image[nr].height,
			                width:  image[nr].width,
			            },{
			                url: image[nr].url,
			                height: image[nr].height * 2.5,
			                width:  image[nr].width * 2.5
			            },{
			                url: image[nr].url,
			                height: image[nr].height * 6.25,
			                width:  image[nr].width * 6.25
			            }]
			        }
				}
			}(i));
			// image URL or path (localhost)
			image[i].src = this.imageArray[i];
			image[i].url = image[i].src;
			console.log(image[i].url);
		};
		anno.makeAnnotatable(this.viewer);

//		if (this.viewer.tileSources.length === this.imageArray.length){
//			this.$loader.hide();
//		};
	},

	createOpenSeadragon: function(){

		this.viewer = OpenSeadragon({
			id: "openseadragon",
			prefixUrl: "images/openseadragon/",
			showNavigator: false,
			tileSources: new Array()
		});
	},

	render: function(pagenumber){

		this.viewer.goToPage(pagenumber-1);
	},
}
