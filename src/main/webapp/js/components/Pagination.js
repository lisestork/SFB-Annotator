var pagination = {

	imageArray: [],
	page: 1,
	paginationStart: 1,
	pagination : [],
	folderSize: 0,
	pageID: "",

	init: function(){
		this.cacheDom();
		this.bindEvents();
	},

	cacheDom: function(){
		this.$el = $('#pagination');
		this.$pagination = this.$el.find('#pageturner');
		this.$active = undefined; //needs to be selected
		this.template = this.$el.find('#pagination-template').html();
	},

	bindEvents: function(){
		events.on('depthChanged', this.hideOrShowPagination.bind(this));
		//onclick event for when previous or next is clicked or a number. refer to navigation function
		this.$el.delegate('a.nav-left', 'click', this.prev.bind(this));
		this.$el.delegate('a.nav-right', 'click', this.next.bind(this));
		this.$el.delegate('a.page', 'click', this.selectPageNr.bind(this));
		this.$el.delegate('#insert', 'click', this.setPageNr.bind(this));
	},

	hideOrShowPagination: function(data){
		if(data.depth == 1){
			this.$el.show(); 
			this.setFolder(eval(itemViewer.path+'.item'), breadCrumbs.selected); 
		} else if (data.depth != 1 && data.oldDepth == 1){
			this.$el.hide();
		};
	},

	setFolder: function(array, path){
		//set everything up for the first image of the folder
		this.paginationStart = 1;
		this.folderSize = array.length;
		this.pagination = ["&nbsp;","&nbsp;","&nbsp;","&nbsp;","&nbsp;"]; //delete pag array
		for (var i=0; i < Math.min(this.folderSize,5); i++){
			this.pagination[i] = eval(this.paginationStart)+i;	
		}
		this.page = 1;
		this.render();
		this.$el.find('[data-toggle="popover"]').popover('show');
	},

	render: function(){
		//set template
		var data = {
				folderDetails: [
					{	
						totalPages: this.folderSize,
						page: this.pagination,
						currentPage: this.page,
					}
				]			
			};
		
			this.$el.html(Mustache.render(this.template, data));
	},

	selectPageNr: function(event){
		var $selected = $(event.target).closest('a'); //search for clicked item
		var i = this.$el.find('a').index($selected);
		this.page = this.$el.find("a:eq("+i+")").html(); 
		this.pageID = this.imageArray[this.page-1];
		var data = {
				page: this.page,
				pageID: this.pageID
			}
		events.emit('pagenumberChanged', data);
		this.$active = $(event.target).closest('li');
		this.render();
	},

	prev: function(){
		if (this.paginationStart != 1){
			this.paginationStart = this.paginationStart -5;
			this.pagination = [eval(this.paginationStart), eval(this.paginationStart)+1, eval(this.paginationStart)+2, eval(this.paginationStart)+3, eval(this.paginationStart)+4];
			this.render();
		};
	},

	next: function(){
		if((this.paginationStart + 5) > this.folderSize){
		// do nothing
		} else {
			this.paginationStart = this.paginationStart + 5;
			this.pagination = ["&nbsp;","&nbsp;","&nbsp;","&nbsp;","&nbsp;"]; //delete pag array
			for (var i=0; i < Math.min((this.folderSize-(this.paginationStart-1)),5); i++){
				this.pagination[i] = eval(this.paginationStart)+i;	
			}
			this.render();
		}
	},

	setPageNr: function(event){
		var value = this.$el.find('#insertedPage').val();
		if (value == ""){
			//nothing happens
		} else {
				this.$el.find('#insertedPage').val("");
			if (value % 5 == 0){
				this.paginationStart = value - (value % 5) - 4;

			} else {
				this.paginationStart = value - (value % 5) + 1;
			};
		this.pagination = ["&nbsp;","&nbsp;","&nbsp;","&nbsp;","&nbsp;"]; //delete pag array
			for (var i=0; i < Math.min((this.folderSize-(this.paginationStart-1)),5); i++){
				this.pagination[i] = eval(this.paginationStart)+i;	
			};
		this.page = value;
		this.pageID = this.imageArray[this.page-1];
		var data = {
			page: this.page,
			pageID: this.pageID
		}
		events.emit('pagenumberChanged', data);
		this.render();
		};
	}
}