var accounts = {
		
		person: [],
		init: function(){
			this.cacheDom();
			this.bindEvents();
		},
		
		cacheDom: function(){
			this.$el = $('#login-modal');
			this.$firstname = this.$el.find('[name="firstname"]');
			this.$lastname= this.$el.find('[name="lastname"]');
			this.$IRI = this.$el.find('[name="IRI"]');
			this.$institution = this.$el.find('[name="institution"]');
			this.$institutionIRI = this.$el.find('[name="institutionIRI"]');
			this.$save = this.$el.find('#save');
			this.$navbar = $('#navigationBar');
			this.$navbarIcon = this.$navbar.find('#loginIcon');
		},
		
		bindEvents: function(){
			this.$save.on('click', this.addParticipant.bind(this));
		},
		
		addParticipant: function(){

			person = {
					firstname: this.$firstname.val(),
					lastname: this.$lastname.val(),
					IRI: this.$IRI.val(),
					institution: this.$institution.val(),
					institutionIRI: this.$institutionIRI.val()
			};
			this.$navbarIcon.html('<span class="glyphicon glyphicon-user" aria-hidden="true"></span> '+this.$firstname.val()+' '+this.$lastname.val()+'@'+this.$institution.val()+' ');
			this.person.push(person);
			var request = new XMLHttpRequest();

			param = "addUser?firstname="+person.firstname+"&lastname="+person.lastname+"&IRI="+person.IRI+"&institution="+person.institution+"&institutionIRI="+person.institutionIRI;
			request.open("POST",param, true);
			request.onreadystatechange = function(e){
				
				if ( request.readyState == 4 && request.status == 200) {

					this.onDataLoaded(request.response)

				}

			}.bind(this);

			request.send();
		},

		onDataLoaded: function(data){

		}
}