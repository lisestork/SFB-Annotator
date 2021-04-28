<!DOCTYPE html>
<html>
<head>
	<meta content="utf-8" http-equiv="encoding">
	<title>Semantic Annotator of Field Books</title>
	<!-- Latest compiled and minified CSS -->
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css"
		integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
	<!-- Optional theme -->
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css"
		integrity="sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp" crossorigin="anonymous">
	<link rel="stylesheet" type="text/css" href="css/loginstyle.css">
	<link href="css/album.css" rel="stylesheet">
	<link href="css/ol.css" rel="stylesheet">
	<link href="css/annotorious-dark.css" rel="stylesheet">
</head>
<body>
	<div class="container-fluid">
		<!-- Navigation -->
		<nav class="navbar navbar-inverse navbar-fixed-top" role="navigation" id="navigationBar">
			<div class="container-fluid">
				<!-- Brand and toggle get grouped for better mobile display -->
				<div class="navbar-header">
					<button type="button" class="navbar-toggle collapsed" data-toggle="collapse"
						data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
						<span class="sr-only">Toggle navigation</span>
						<span class="icon-bar"></span>
						<span class="icon-bar"></span>
						<span class="icon-bar"></span>
					</button>
					<a class="navbar-brand" href="#">Semantic Field Book Annotator</a>
				</div>
				<!-- Collect the nav links, forms, and other content for toggling -->
				<div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
					<ul class="nav navbar-nav">
						<li><a role="button" href="#" rel="about"><span class="glyphicon glyphicon-comment"
									aria-hidden="true"></span> About</a></li>
						<li><a role="button" href="#" rel="collections"><span class="glyphicon glyphicon-th"
									aria-hidden="true"></span> Collections</a></li>
						<li><a role="button" href="#" rel="register"><span class="glyphicon glyphicon-user"
									aria-hidden="true"></span> Register</a></li>
						<li>
						</li>
					</ul>
					<span class="navbar-text navbar-right" id="loginIcon"></span>
				</div><!-- /.navbar-collapse -->
			</div><!-- /.container-fluid -->
		</nav>
		<section id="register" style="display:block">
			<div class="row">
				<section class="jumbotron text-center">
					<div class="container">
						<h3 class="jumbotron-heading">
							<span class="glyphicon glyphicon-user" aria-hidden="true"></span>
							Register <a href="#" data-toggle="modal" data-target="#login-modal">here</a> to proceed with annotation.
						</h3>
					</div>
				</section>
			</div>
		</section>
		<!-- About -->
		<section id="about" style="display:none">
			<div class="row">
				<section class="jumbotron text-center">
					<div class="container">
						<h3 class="jumbotron-heading"><span class="glyphicon glyphicon-comment"
								aria-hidden="true"></span> About</h3>
						<p class="lead text-muted">Workflow SFB-Annotator.</p>
					</div>
				</section>
			</div>
			<div class="row">
				<div class="container">
					<image src="images/workflow2.png" class="img-responsive center-block"></image>
				</div>
			</div>
		</section>
		<!--Collections -->
		<section id="collections" class="container-fluid" style="display:none">
			<!-- Jumbotron -->
			<div class="row">
				<section class="jumbotron text-center">
					<div class="container" id="collectionsJumbotron">
						<h3 class="jumbotron-heading"><span class="glyphicon glyphicon-th" aria-hidden="true"></span>
							Collections</h3>
					</div>
				</section>
				<!-- BreadCrumbs -->
				<div id="breadcrumbViewer">
					<script id="breadcrumb-template" type="text/template">
							<ol class="breadcrumb">
								{{#inactive}}
									<li><a href="#" class="inactive">{{crumb}}</a></li>	
								{{/inactive}}
								{{#active}}
									<li class="active">{{crumb}}</li>
								{{/active}}
							</ol>
						</script>
				</div>
				<!--Folder viewer -->
				<div class="album text-muted" id="folderViewer">
					<script id="folder-template" type="text/template">
								{{#collectionDetails}}
									<div class='card'>
										<a href='#' class='thumbnail text-center'>
											<img src='{{url}}' alt='manuscript'><br><p  class="small">{{name}}<br>
											<b>{{id}}</b></p><span class='badge'>{{items}}</span>
										</a>
									</div>
								{{/collectionDetails}}
						</script>
				</div>
			</div>
			<!--div row -->
			<!--Image viewer -->
			<div id="container-fluid" id="imageViewer">
				<div class="loading" id="loading" style="display:none"></div>
				<div id="pagination" style="display:none">
					<script id="pagination-template" type="text/template">
							{{#folderDetails}}
							<div class="container-fluid">
								<div class="row-fluid">
							  <div class="col-md-12">
							  	<div class="row">
							  		<div class="col-md-5">
												<ul class="pagination" id="pageID">
													<li><a class="disabled" href="#">Page {{currentPage}} of {{totalPages}}</a></li>
												</ul>
											</div>
							    <div class="col-md-5">
							    	<ul class="pagination" id="pageturner">
									    <li><a class="nav-left" href="#"><span>&laquo;</span> Prev </a></li>
   										<li><a class="page" data-toggle="popover" data-placement="top" data-html="true" data-content="<p class='text-info'><small>Start by selecting a page from the folder</small></p>" href="#">{{{page.0}}}</a></li>									    
													<li><a class="page" href="#">{{{page.1}}}</a></li>
									    <li><a class="page" href="#">{{{page.2}}}</a></li>
									    <li><a class="page" href="#">{{{page.3}}}</a></li>
									    <li><a class="page" href="#">{{{page.4}}}</a></li>
									   	<li><a class="nav-right" href="#"> Next <span>&raquo;</span></a></li>
												</ul>
							    </div>
							    <div class="col-md-2">
							    	<div class="input-group">
													<input class="form-control" id="insertedPage" type="text" placeholder="Go to page..">
													<span class="input-group-btn">
									     <button id="insert" class="btn btn-default" type="button">
															<span class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>
														</button>
													</span>
												</div>
								 		</div>
							  	</div>
							  </div>
							 </div>
							</div>
							{{/folderDetails}}
					</script>
				</div>
				<div class="row" id="image" style="display:none;position:relative;">
					<div class="col-md-12">
						<a href="#" id="annotate"> Annotate <span class="glyphicon glyphicon-tags"
								aria-hidden="true"></span></a>
						<div id="openseadragon" class="openseadragon"></div>
					</div>
					<div class="col-md-0 bg-faded" id="table" style="display:none">
						<div>
							<div class="row" style="background-color:#ccc">
								<h4><small class="text-muted">Annotated Concepts</small></h4>
							</div>
							<div class="row">
								<ul class="list-group" id="list">
								</ul>
							</div>
						</div>
					</div>
				</div>
			</div>
		</section> <!-- section collections -->
	</div>
	<!--container-fluid-->
	<!-- Login Screen -->
	<div class="modal fade" id="login-modal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
		aria-hidden="true" style="display: none;">
		<div class="modal-dialog">
			<div class="loginmodal-container">
				<h1>Register</h1><br>
				<form>
					<input type="text" name="IRI" placeholder="IRI (e.g. ORCID)"
						value="http://orcid.org/0000-0002-2146-4803">
					<button id="save" type="button" class="btn btn-default" data-dismiss="modal">Save</button>
				</form>
			</div>
		</div>
	</div>

	<!-- Libs -->

	<script src="js/libs/jquery-3.0.0.min.js" crossorigin="anonymous"></script>
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"
		integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa"
		crossorigin="anonymous"></script>
	<script src="js/libs/mustache.js"></script>
	<script src="js/libs/annotorious.debug.js"></script>
	<script src="js/libs/openseadragon.min.js"></script>
	<script src="js/libs/annotorious.min.js"></script>
	<script src="js/libs/fancy_box_selector.js"></script>
	<script src="js/libs/ol.js"></script>
	<script src="js/libs/require.js"></script>
	<!-- <script src="js/libs/awesomeplete.js"></script> -->

	<script>
		//jQuery.noConflict();
		require.config({
			waitSeconds: 2,
			paths: {
				text: 'js/libs/text', //text is required
				json: 'js/libs/json' //alias to plugin
			}
		});
	</script>

	<!-- Components -->
	<script src="js/components/ItemViewer.js"></script>
	<script src="js/components/ImageViewer.js"></script>
	<script src="js/components/Pagination.js"></script>
	<script src="js/components/BreadCrumbs.js"></script>
	<script src="js/components/Navigation.js"></script>
	<script src="js/components/Events.js"></script>
	<script src="js/components/Accounts.js"></script>
	<script src="js/components/semanticAnnotation.js"></script>
	<script src="js/App.js"></script>

	<script src="js/main.js"></script>

</body>

</html>