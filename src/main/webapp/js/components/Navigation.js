var Navigation = {
	
	init: function(){
		this.cacheDom();
		this.bindEvents();
		this.render();
	},

	cacheDom: function(){
		this.$el = $('#navigationBar');
		this.$ul = this.$el.find('ul');
	},

	bindEvents: function(){
		this.$ul.find('a').on("click", this.selectedItem.bind(this));
	},

	render: function(){
		//content does not change
	},

	selectedItem: function(event){

		var $selected = $(event.target).closest('a'); //search for clicked item
		var target = $($selected).attr('rel');
		$("#"+target).show().siblings("section").hide();
		events.emit('navbarSelection', target);

	}	   
}


	// this.selectedImage = new Image();
	// this.selectedImage.onload = function() {
	//   alert(this.width + 'x' + this.height);
	// }
	// this.selectedImage.src = 'Images/MAKING_SENSE.jpg';
	// this.viewer = OpenSeadragon({
	//   id: "openseadragon",
	//   prefixUrl: "Images/openseadragon/",
	//   showNavigator: false,
	//   tileSources: {
	//     type: 'legacy-image-pyramid',
	// 	    levels:[{
	// 	      url: 'Images/MAKING_SENSE.jpg',
	// 	      height: this.selectedImage.height,
	// 	      width:  this.selectedImage.width
	// 	    },{
	// 	      url: 'Images/MAKING_SENSE.jpg',
	// 	      height: this.selectedImage.height * 2.5,
	// 	      width:  this.selectedImage.width * 2.5
	// 	    },{
	// 	      url: 'Images/MAKING_SENSE.jpg',
	// 	      height: this.selectedImage.height * 6.25,
	// 	      width:  this.selectedImage.width * 6.25
	// 	    }]
	//   }
	// });