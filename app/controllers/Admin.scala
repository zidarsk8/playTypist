package controllers

import com.mongodb.casbah.Imports.ObjectId
import play.api._
import play.api.mvc._
import models._
import play.api.data._
import play.api.data.Forms._


object Admin extends Controller with UserTrait {
  
  val MIN_ARTICLE_LENGTH = 250;
  
  def articleForm = Form(
    mapping(
      "text" -> nonEmptyText,
      "author" -> nonEmptyText,
      "source" -> nonEmptyText)
      ((text, author, source) => Article(new ObjectId, text, author, source))
     ((article: Article) => Some((
         article.text, 
         article.author, 
         article.source)))
      verifying ("Sorry, this text is too short for this app.", result => result match {
        case Article(_,text, _, _) => (true)
    })
  )

  def userList = Action { implicit request =>
    Ok(views.html.userList(Users.all))
  }
  
  def articleList = Action { implicit request =>
    Ok(views.html.articleList(Corpus.all))
  }
  
  def insertNewArticle = Action { implicit request =>
    articleForm.bindFromRequest.fold(
      form => BadRequest(views.html.newArticle(form)),
      article => {
        Corpus.create(article)
        Redirect(routes.Admin.articleList()).flashing("message" -> "Article added!")
      }
    )
  }
  
  def newArticle = Action { implicit request =>
    Ok(views.html.newArticle(articleForm))
  }

  def removeUser(id: String) = Action { implicit request =>

    val flash = user map { user => 
      Users.remove(id)
      ("message" -> "User Deleted!")
    }getOrElse{
  	  ("error" -> "You can't do that! You're a bad perosn")
  	}
    Redirect(routes.Admin.userList()).flashing(flash)
  }
 

  def disableArticle(id: String) = Action { implicit request =>
  	//TODO: dissable article
    Redirect(routes.Admin.articleList()).flashing(("message" -> "Article has been dissabled."))
  }
  
  def removeArticle(id: String) = Action { implicit request =>
    
    val flash = user map { user => 
      Corpus.remove(id)
      ("message" -> "Article Deleted! Corpus is now tiny!")
    }getOrElse{
  	  ("error" -> "You can't do that! You're a bad perosn")
  	}
    Redirect(routes.Admin.articleList()).flashing(flash)
  }

}
