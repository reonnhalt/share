package controllers

import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import models._
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import play.api.db.DB

import play.api.libs.json.{JsObject, _}
import play.api.libs.json.Json

case class dateForm(display_date: String)

class TimeCardController extends Controller with Secured
{
    val form = Form (
      mapping (
      "display-date"  -> text
      )(dateForm.apply)(dateForm.unapply)
    )

    // タイムカード編集画面表示
    def show(symbol:String,employee_number:String,year: String,month: String) = withUser { user => implicit request =>
        var employee_id = 0
        var exist = true
        var disp_year = year
        var disp_month = month
        //URL等に直接入力された時用にチェック処理
        if(Validate.ValidateYear(year) == false || Validate.ValidateMonth(month) == false){
            disp_year = CalendarDates.GetNowYear
            disp_month = CalendarDates.GetNowMonth
        }
        try{
            //URLの従業員番号から従業員IDを取得
            employee_id = Employees.takeEmployeeID(employee_number)
        }
        catch{
            case e: Exception =>
            //存在しないユーザーの場合はセッションから取得
            employee_id = Employees.GetEmployeeId(user.id)
            exist = false
        }
        if(exist){
            Ok(views.html.user.timecardEdit(user.id, year+"-"+month))
        }
        else{
            Ok(views.html.failure(user.id,"警告 : 対象のユーザーは存在しません"))
        }
    }
    
    // 表示日付変更
    def changeDate = withUser { user => implicit request =>
        val display_date = form.bindFromRequest.get.display_date
        val years = display_date.split('-').toList
        val year  = years.head
        val month = years.last
        val employee_number = Employees.getEmployeeCode(user.id)
        //バージョン情報
        val symbol = Play.application.configuration.getString("version.symbol").get
        //リダイレクト
        Redirect(routes.TimeCardController.show(symbol, employee_number, year, month))
    }

    //タイムカードの情報をjsonで取得
    def getList = withUser { user => implicit request =>
        //jsonデータの取得
        val jsonObj   = Json.stringify(request.body.asJson.get)
        val jsonData  = Json.parse(jsonObj)
        
        val employee_id = Employees.GetEmployeeId(user.id)
        val strPeriod = (jsonData \ "date").as[String] + "-01"
        val endPeriod = (jsonData \ "date").as[String] + "-31"
         
        val time_card_info = TimeCard.getList(employee_id, strPeriod, endPeriod)
        val sendJson = Json.toJson(time_card_info)

        Ok(sendJson)
    }
    //出社時刻の更新
    def updateArrival = withUser { user => implicit request => 
    
        //jsonデータの取得
        val jsonObj   = Json.stringify(request.body.asJson.get)
        val jsonData  = Json.parse(jsonObj)
        
        //必要なデータを取得
        val date = (jsonData \ "date").as[String]
        val arrival = (jsonData \ "arrival").as[String]
        val employee_id = Employees.GetEmployeeId(user.id) 
        val edit_by     = User.getUserID(user.id).get
        
        //タイムカードデータの存在チェック
        if(!TimeCard.exist(employee_id, date)){
            //存在しない場合は新規作成
            TimeCard.create(employee_id, edit_by)
        }
        
        //更新処理
        val info = TimeCard.getInfo(employee_id, date)
        val id = info.id.get
        val arrival_time = date + " " + arrival + ":00"
        val leave_time = info.leave_time.get
        val final_arrival_time = arrival_time
        val final_leave_time = info.final_leave_time.get

        TimeCard.update(id, employee_id, arrival_time, leave_time, final_arrival_time, final_leave_time, edit_by)

        Ok("success")
    }

    //退社時刻の更新
    def updateLeave = withUser { user => implicit request => 
        
        //jsonデータの取得
        val jsonObj   = Json.stringify(request.body.asJson.get)
        val jsonData  = Json.parse(jsonObj)
        
        //必要なデータを取得
        val date = (jsonData \ "date").as[String]
        val leave = (jsonData \ "leave").as[String]
        val employee_id = Employees.GetEmployeeId(user.id) 
        val edit_by     = User.getUserID(user.id).get
        
        //更新処理
        val info = TimeCard.getInfo(employee_id, date)
        val id = info.id.get
        val arrival_time = info.arrival_time.get
        val leave_time = date + " " + leave + ":00"
        val final_arrival_time = info.final_arrival_time.get
        val final_leave_time = leave_time

        TimeCard.update(id, employee_id, arrival_time, leave_time, final_arrival_time, final_leave_time, edit_by)

        Ok("success")
    }

    // タイムカード確認画面表示(個人)
    def showChart = withUser { user => implicit request =>
        Ok(views.html.user.showChart(user.id))
    }

    // タイムカード確認画面表示(個人)
    def showTogether = withUser { user => implicit request =>
        Ok(views.html.user.showTogether(user.id))
    }

    //特定日付の総合情報を取得
    def getInfoByDate = withUser { user => implicit request =>
         
        val today = CalendarDates.GetToday
        val info_by_date = TimeCard.getInfoByDate(today)
        val sendJson = Json.toJson(info_by_date)

        Ok(sendJson)
    }
}
