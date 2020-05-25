var semanticAnnotation = {

		annotatedPersons: [],
		annotatedTaxa: [],
		annotatedLocations: [],
		annotatedAnEntities: [],
		annotatedProperties: [],
		organismIDs: [],

		init: function(){
			this.cacheDom();
			this.bindEvents();
			this.render();
			this.addPlugins();
		},

		cacheDom: function(){
			this.$el = $('#image');
		},

		bindEvents: function(){
			events.on('pagenumberChanged', function(data){
				this.pageID = data.pageID;
			}.bind(this));
			anno.addHandler('onAnnotationCreated', function(annotation){
				//this.storeAnnotationSQL(annotation);
				this.storeAnnotationRDF(annotation);
			}.bind(this));
			anno.addHandler('onAnnotationRemoved', function(annotation){
				//this.removeAnnotationSQL(annotation);
				this.removeAnnotationRDF(annotation);
			}.bind(this));
			events.on('folderSelected', this.loadAnnotations.bind(this));
			events.on('viewerOpened', this.showAnnotations.bind(this));
		},

		render: function(){

		},

		// storeAnnotationSQL: function(annotation){
		//
		// 	var request = new XMLHttpRequest();
		// 	param = "writeAnnotationsToSQL?url="+annotation.source+"&firstname="+accounts.person[0].firstname+"&lastname="+accounts.person[0].lastname+"&class="+annotation.text.toLowerCase().trim()+"&x="+annotation.shapes[0].geometry.x+"&y="+annotation.shapes[0].geometry.y+"&height="+annotation.shapes[0].geometry.height+"&width="+annotation.shapes[0].geometry.width+"&organismID="+annotation.organismID+"&action="+accounts.$action;
		// 	request.open("POST",param, true);
		// 	request.onreadystatechange = function(e){
		//
		// 		if ( request.readyState == 4 && request.status == 200) {
		// 			this.loadAnnotations();
		// 		}
		//
		// 	}.bind(this);
		//
		// 	request.send();
		// },
		//
		// removeAnnotationSQL: function(annotation){
		// 	var request = new XMLHttpRequest();
		// 	param = "removeAnnotationsFromSQL?url="+this.pageID+"&firstname="+accounts.person[0].firstname+"&lastname="+accounts.person[0].lastname+"&class="+annotation.text+"&x="+annotation.shapes[0].geometry.x+"&y="+annotation.shapes[0].geometry.y+"&height="+annotation.shapes[0].geometry.height+"&width="+annotation.shapes[0].geometry.width+"&action="+accounts.$action;
		// 	request.open("POST",param, true);
		// 	request.onreadystatechange = function(e){
		//
		// 		if ( request.readyState == 4 && request.status == 200) {
		// 			this.loadAnnotations();
		// 		}
		//
		// 	}.bind(this);
		//
		// 	request.send();
		// },

		removeAnnotationRDF: function(annotation){

			selector = '#xywh='+annotation.shapes[0].geometry.x+','+annotation.shapes[0].geometry.y+','+annotation.shapes[0].geometry.width+','+annotation.shapes[0].geometry.height;
			selectorURI = encodeURIComponent(selector);
			var request = new XMLHttpRequest();
			param = "removeAnnotationsFromRDF?source="+annotation.source+"&selector="+selectorURI+"&organismID"+annotation.organismID;
			request.open("POST",param, true);
			request.onreadystatechange = function(e){

				if ( request.readyState == 4 && request.status == 200) {
				}

			}.bind(this);

			request.send();
		},

		loadAnnotations: function(folder){
			this.annotations = [];
			var that = this;
			var request = new XMLHttpRequest();
			param = "loadAnnotations?firstname="+accounts.person[0].firstname+"&lastname="+accounts.person[0].lastname+"&action="+accounts.$action;
			request.open("POST",param, true);
			request.onreadystatechange = function(e){
				if ( request.readyState == 4 && request.status == 200) {

					var response = request.response;
					response = response.split(';');
					var annotations = response[1].split('\n');
					var amount = response[0];
					for(var i=0; i< amount; i++){
						var annotation = annotations[i].split(',')

						that.annotations.push({
							source: annotation[0],
							text: annotation[1],
							x: parseFloat(annotation[2]),
							y: parseFloat(annotation[3]),
							width: parseFloat(annotation[4]),
							height:  parseFloat(annotation[5]),
							organismID: annotation[6]
						});
					};
				}

			};
			request.send();
		},

		showAnnotations: function(page){
			this.annotations.forEach(function(element){
				if(element.source == page){
					anno.addAnnotation({
					    src : 'dzi://openseadragon/something',
					    source : element.source,
					    text : element.text,
					    shapes : [{
					        type : 'rect',
					        geometry : { x : element.x, y: element.y, width : element.width, height: element.height }
					    }],
					    organismID: element.organismID
					});
				}
			})
		},

		storeAnnotationRDF: function(annotation){

			//mandatory annotation info
				//annotation.date = new Date().toJSON().slice(0,10);
				annotation.date = "2017-09-21";
			    annotation.annotator = accounts.person[0].IRI;

			    //target
			    annotation.source = this.pageID;
			    annotation.selector = '#xywh='+annotation.shapes[0].geometry.x+','+annotation.shapes[0].geometry.y+','+annotation.shapes[0].geometry.width+','+annotation.shapes[0].geometry.height;

			    //textual body
			    annotation.verbatim = $('#verbatim').val();
			    annotation.language = $('#language').val();

			//semantic annotation info
			    annotation.type = annotation.text.toLowerCase().trim();
			    annotation.property = $('select[name=property]').val();
			    annotation.organismID = "-";

			    if(annotation.property === "hasIdentification"){
					annotation.belongstotaxon = $('#belongstotaxon').val();
					annotation.rank = $('#rank').val();
					annotation.person = $('#identifiedBy').val();
					annotation.organismID = $('#organismID').val();
				} else if (annotation.property === 'additionalIdentification'){
					annotation.organismID = $('#organismID').val();
					annotation.identificationID = $('#identificationID').val();
					annotation.belongstotaxon = $('#belongstotaxon').val();
					annotation.rank = $('#rank').val();
				} else if (annotation.property === 'verbatimEventDate'){
					annotation.year = $('#year').val();
					annotation.month = $('#month').val();
					annotation.day = $('#day').val();
					annotation.organismID = $('#organismID').val();
				} else if (annotation.property === 'measuresOrDescribes'){
					if ($('#type').val() === 'propertyorattribute'){
						annotation.propertyorattribute = $('#propertyorattribute').val();
					} else if ($('#type').val() ==="anatomicalentity"){
						annotation.anatomicalentity = $('#anatomicalentity').val();
					} annotation.organismID = $('#organismID').val();
				} else if (annotation.property === "basedOn"){
					annotation.organismID = $('#organismID').val();
				} else if (annotation.property === "locatedAt"){
					annotation.geonamesfeature = $('#geonamesfeature').val();
					annotation.organismID = $('#organismID').val();
				} else if (annotation.property == "additionalLocatedAt"){
					annotation.geonamesfeature = $('#geonamesfeature').val();
					annotation.organismID = $('#organismID').val();
					annotation.occurrenceID = $('#occurrenceID').val();
				} else if (annotation.property === 'scientificNameAuthorship'){
					annotation.person = $('#person').val();
					annotation.belongstotaxon = $('#belongstotaxon').val();
				} else if (annotation.property === 'identifiedBy'){
					annotation.person = $('#person').val();
					annotation.organismID = $('#organismID').val();
				} else if (annotation.property == 'recordedBy'){
					annotation.person = $('#person').val();
					annotation.organismID = $('#organismID').val();
				} else if (annotation.property === 'additionalRecordedBy'){
					annotation.person = $('#person').val();
					annotation.organismID = $('#organismID').val();
					annotation.occurrenceID = $('#occurrenceID').val();
				} else if (annotation.property === "type" && annotation.type != 'taxon'){
					annotation.instance = $('#instance').val();
				} else if (annotation.property === "type" && annotation.type === 'taxon'){
					annotation.belongstotaxon = $('#belongstotaxon').val();
					annotation.rank = $('#rank').val();
				}

			    annoString = JSON.stringify(annotation);

			  	annoURI = encodeURIComponent(annoString);

				var request = new XMLHttpRequest();
				param = "writeAnnotationsToRDF?annotation="+annoURI;
				request.open("POST",param, true);
				request.onreadystatechange = function(e){

					if ( request.readyState == 4 && request.status == 200) {
					}

				}.bind(this);

				request.send();

				//this.storeAnnotationSQL(annotation);
		},

		addPlugins: function(){
			//Create Plugin
			annotorious.plugin.annoInformationPlugin = function(opt_config_options) { }

			annotorious.plugin.annoInformationPlugin.prototype.initPlugin = function(anno) {
			  // Add initialization code here, if needed (or just skip this method if not)
			}

			annotorious.plugin.annoInformationPlugin.prototype.onInitAnnotator = function(annotator) {
			  // A Field can be an HTML string or a function(annotation) that returns a string

				annotator.popup.addField(function(annotation) {
					return '<em> <span class="badge"> organismID:' + annotation.organismID + '</span></em>'
				  });

				annotator.editor.addField(function(annotation) {
					var
						field = '<input class="annotorious-editor-text" id="verbatim" placeholder="verbatim text" value ="" style="width:160px;height:20px;display:inline;">'
						field = field + '<input class="annotorious-editor-text" id="language" placeholder="language" value ="" style="width:40px;height:20px;display:inline;"><br>'
						field = field + '<select name="property" onchange="semanticAnnotation.addFieldsToEditor($(\'select[name=property]\').val())" class="annotorious-editor-text" style="width:160px;height:20px;display:inline;"><option value="">Select property..</option><option value="type">Type:</option><option value="hasIdentification">Organism identification to:</option><option value="additionalIdentification">Additional identification to:</option><option value="identifiedBy">Organism identified by:</option><option value="recordedBy">Occurrence recorded by:</option><option value="additionalRecordedBy">Additional occurrence recorded by:</option>"><option value="scientificNameAuthorship">Author of scientific name:</option><option value="locatedAt">Occurrence located at:</option><option value="additionalLocatedAt">Additional occurrence located at:</option><option value="verbatimEventDate">Organism described on:</option><option value="basedOn">Identification based on (table):</option><option value="measuresOrDescribes">Table/paragraph measures or describes:</option></select>'
						field = field +  '<input class="annotorious-editor-text" id="type" placeholder="type" value ="" style="width:40px;height:20px;display:inline;"><br>'
						field = field + '<span id="taxonfield1"></span>'
						field = field + '<span id="taxonfield2"></span>'
						field = field + '<span id="taxonfield3"></span>'
						field = field + '<span id="datefield1"></span>'
						field = field + '<span id="datefield2"></span>'
						field = field + '<span id="datefield3"></span>'
						field = field + '<span id="anatomicalentityfield1"></span>'
						field = field + '<span id="propertyorattributefield1"></span>'
						field = field + '<span id="geonamesfeaturefield1"></span>'
						field = field + '<span id="organismIDfield1"></span>'
						field = field + '<span id="occurrenceIDfield1"></span>'
						field = field + '<span id="identificationIDfield1"></span>'
						field = field + '<span id="instancefield1"></span>'
						field = field + '<span id="personfield1"></span>'
					return field

				});
			}

			//Add the plugin
			anno.addPlugin('annoInformationPlugin', {});
		},

		addClassToField: function(type){
			$('#type').val(type);
		},

		addFieldsToEditor: function(property){

			//empty all fields
			$('#taxonfield1').html("");
			$('#taxonfield2').html("");
			$('#taxonfield3').html("");
			$('#datefield1').html("");
			$('#datefield2').html("");
			$('#datefield3').html("");
			$('#anatomicalentityfield1').html("");
			$('#propertyorattributefield1').html("");
			$('#geonamesfeaturefield1').html("");
			$('#organismIDfield1').html("");
			$('#occurrenceIDfield').html("");
			$('#identificationIDfield1').html("");
			$('#instancefield1').html("");
			$('#personfield1').html("");

			if(property === "hasIdentification"){
				$('#taxonfield1').html('<input class="annotorious-editor-text" id="belongstotaxon" placeholder="Belongs to taxon:" value =""  style="width:250px;height:20px;">');
				$('#taxonfield2').html('<input class="annotorious-editor-text" id="rank" placeholder="Taxon rank:" value =""  style="width:250px;height:20px;">');
				$('#taxonfield3').html('<input class="annotorious-editor-text" id="identifiedBy" placeholder="Identified By:" value =""  style="width:250px;height:20px;">');
				$('#organismIDfield1').html('<input class="annotorious-editor-text" id="organismID" placeholder="Organism ID:" value ="" style="width:250px;height:20px;">');
			} else if (property === 'additionalIdentification'){
				$('#organismIDfield1').html('<input class="annotorious-editor-text" id="organismID" placeholder="Organism ID:" value ="" style="width:250px;height:20px;">');
				$('#identificationIDfield1').html('<input class="annotorious-editor-text" id="identificationID" placeholder="Occurrence ID:" value ="" style="width:250px;height:20px;">');
				$('#taxonfield1').html('<input class="annotorious-editor-text" id="belongstotaxon" placeholder="Belongs to taxon:" value =""  style="width:250px;height:20px;">');
				$('#taxonfield2').html('<input class="annotorious-editor-text" id="rank" placeholder="Taxon rank:" value =""  style="width:250px;height:20px;">');
			} else if (property === 'verbatimEventDate'){
				$('#datefield1').html('<input class="annotorious-editor-text" id="year" placeholder="Year (yyyy):" value ="0" style="width:250px;height:20px;">');
				$('#datefield2').html('<input class="annotorious-editor-text" id="month" placeholder="Month (mm):" value ="0" style="width:250px;height:20px;">');
				$('#datefield3').html('<input class="annotorious-editor-text" id="day" placeholder="Day (dd):" value ="0" style="width:250px;height:20px;">');
				$('#organismIDfield1').html('<input class="annotorious-editor-text" id="organismID" placeholder="Organism ID:" value ="" style="width:250px;height:20px;">');
			} else if (property === 'basedOn'){
				$('#organismIDfield1').html('<input class="annotorious-editor-text" id="organismID" placeholder="Organism ID:" value ="" style="width:250px;height:20px;">');
			} else if (property === 'measuresOrDescribes'){
				$('#organismIDfield1').html('<input class="annotorious-editor-text" id="organismID" placeholder="Organism ID:" value ="" style="width:250px;height:20px;">');
				if ($('#type').val() === 'propertyorattribute'){
					$('#propertyorattributefield1').html('<input class="annotorious-editor-text" id="propertyorattribute" placeholder="ncit:propertyorattribute subclass IRI:" value ="" style="width:250px;height:20px;">');
				} else if ($('#type').val() ==="anatomicalentity"){
					$('#anatomicalentityfield1').html('<input class="annotorious-editor-text" id="anatomicalentity" placeholder="uberon:anatomicalentity subclass IRI:" value ="" style="width:250px;height:20px;">');
				}
			} else if (property == "locatedAt"){
				$('#geonamesfeaturefield1').html('<input class="annotorious-editor-text" id="geonamesfeature" placeholder="gn:geonamesfeature IRI:" value ="" style="width:250px;height:20px;">');
				$('#organismIDfield1').html('<input class="annotorious-editor-text" id="organismID" placeholder="Organism ID:" value ="" style="width:125px;height:20px;display:inline;">');
			} else if (property == "additionalLocatedAt"){
				$('#geonamesfeaturefield1').html('<input class="annotorious-editor-text" id="geonamesfeature" placeholder="gn:geonamesfeature IRI:" value ="" style="width:250px;height:20px;">');
				$('#organismIDfield1').html('<input class="annotorious-editor-text" id="organismID" placeholder="Organism ID:" value ="" style="width:250px;height:20px;">');
				$('#occurrenceIDfield1').html('<input class="annotorious-editor-text" id="occurrenceID" placeholder="Occurrence ID:" value ="" style="width:250px;height:20px;">');
			} else if (property === 'scientificNameAuthorship'){
				$('#personfield1').html('<input class="annotorious-editor-text" id="person" placeholder="viaf IRI:" value ="" style="width:250px;height:20px;">');
				$('#taxonfield1').html('<input class="annotorious-editor-text" id="belongstotaxon" placeholder="Belongs to taxon:" value =""  style="width:250px;height:20px;">');
			} else if (property == 'identifiedBy'){
				$('#personfield1').html('<input class="annotorious-editor-text" id="person" placeholder="viaf IRI:" value ="" style="width:250px;height:20px;">');
				$('#organismIDfield1').html('<input class="annotorious-editor-text" id="organismID" placeholder="Organism ID:" value ="" style="width:250px;height:20px;">');
			} else if (property == 'recordedBy'){
				$('#personfield1').html('<input class="annotorious-editor-text" id="person" placeholder="viaf IRI:" value ="" style="width:250px;height:20px;">');
				$('#organismIDfield1').html('<input class="annotorious-editor-text" id="organismID" placeholder="Organism ID:" value ="" style="width:250px;height:20px;">');
			} else if (property === 'additionalRecordedBy'){
				$('#personfield1').html('<input class="annotorious-editor-text" id="person" placeholder="viaf IRI:" value ="" style="width:250px;height:20px;">');
				$('#organismIDfield1').html('<input class="annotorious-editor-text" id="organismID" placeholder="Organism ID:" value ="" style="width:250px;height:20px;">');
				$('#occurrenceIDfield1').html('<input class="annotorious-editor-text" id="occurrenceID" placeholder="Occurrence ID:" value ="" style="width:250px;height:20px;">');
			} else if (property == "type" && $('#type').val() != 'taxon'){
				$('#instancefield1').html('<input class="annotorious-editor-text" id="instance" placeholder="Instance:" value ="" style="width:250px;height:20px;">');
			} else if (property == "type" && $('#type').val() == 'taxon'){
				$('#taxonfield1').html('<input class="annotorious-editor-text" id="belongstotaxon" placeholder="Belongs to taxon:" value =""  style="width:250px;height:20px;">');
				$('#taxonfield2').html('<input class="annotorious-editor-text" id="rank" placeholder="Taxon rank:" value =""  style="width:250px;height:20px;">');
			}
		}

}
