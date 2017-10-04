var breadCrumbs = {
	
	path: ['itemViewer'],
	selected: ["Collections"],

	init: function(){

		this.cacheDom();
		this.bindEvents();
		this.render();
	},

	cacheDom: function(){
		this.$el = $('#breadcrumbViewer')
		this.template = this.$el.find('#breadcrumb-template').html();
	},

	bindEvents: function(){
		this.$el.delegate('a', 'click', this.breadcrumbSelected.bind(this)); //if crumb is selected
		var that = this; 
		events.on('itemSelected', function(data){
			that.selected.push(data);
			that.render();
		});
		events.on('navbarSelection', function(target){
			if(target == "collections"){
				this.selected = ["Collections"];
				this.render();
				}
		}.bind(this));
	},

	render: function() {	
		//render breadcrums
		var data = {
			inactive: [], //first render, no inactive crumbs 
			active: [{crumb: this.selected[this.selected.length-1] }] //active crumb always last in array of selected folders
		};
		if(this.selected.length > 1){ 
		//if there are more than 1 selections, push all selected crumbs minus last to inactive
			for(var i = 0; i < this.selected.length-1; i++){
				data.inactive.push( { crumb: this.selected[i] } );
			};
		};		
		this.$el.html(Mustache.render(this.template, data)); //re-render template
	},

	breadcrumbSelected: function(event){
		//change breadcrumbs
		var $selected = $(event.target).closest('a'); 
		var i = this.$el.find('a').index($selected);
		//remove breadcrumbs
		var max = this.selected.length;
		this.selected.splice(i+1, max-i);//this.selected = array.splice(i+1,max-i);
		//emit data
		var diff = max - this.selected.length;
		events.emit('breadCrumbSelected', diff);
		//re-render
		this.render();
	}
}