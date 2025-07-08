package client_system;

import java.awt.Dialog;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;											// @2
import java.text.ParseException;										// @2
import java.text.SimpleDateFormat;										// @2
import java.time.LocalDate;
import	java.util.ArrayList;											// @1
import java.util.Calendar;												// @2

public class ReservationControl {
	// MySQLに接続するためのデータ
	Connection	sqlCon;
	Statement	sqlStmt;
	String		sqlUserID	= "puser";									// ユーザID
	String		sqlPassword	= "1234";									// パスワード
	// 予約システムのユーザID及びLogin状態
	String		reservationUserID;
	private	boolean	flagLogin;											// ログイン状態(ログイン済:true)
	
	// ReservationControlクラスのコンストラクタ
	ReservationControl(){
		flagLogin = false;
	}
	
	// MySQLに接続するためのメソッド
	private	void	connectDB() {
		try {
			Class.forName( "org.gjt.mm.mysql.Driver");					// MySQLのドライバをLoadする
			// MySQLに接続
			String	url = "jdbc:mysql://localhost?useUnicode=true&characterEncoding=SJIS";
			sqlCon	= DriverManager.getConnection( url, sqlUserID, sqlPassword);
			sqlStmt	= sqlCon.createStatement();							// Statement Objectを生成
		} catch(Exception e) {											// 例外発生時
			e.printStackTrace();										// Stack Traceを表示
		}
	}
	
	//// MySQLから切断するためのメソッド
	private	void	closeDB() {
		try {
			sqlStmt.close();											// Statement ObjectをCloseする
			sqlCon.close();												// MySQLとの接続を切る
		} catch( Exception e) {											// 例外発生時
			e.printStackTrace();										// StackTraceを表示
		}
	}
	
	//// ログイン・ログアウトボタンの処理
	public	String	loginLogout( MainFrame frame) {
		String	res = "";												// 結果表示エリアのメッセージをNullを結果で初期化
		if( flagLogin) {
			flagLogin	= false;
			frame.buttonLog.setLabel( " ログイン ");
			frame.tfLoginID.setText( "未ログイン");
		} else {
			// ログインダイアログ生成＋表示
			LoginDialog	ld	= new LoginDialog(frame);
			ld.setBounds( 100, 100, 350, 150);							// Windowの位置とサイズ設定
			ld.setResizable( false);									// Windowをサイズ固定化
			ld.setVisible( true);										// Windowを可視化
			ld.setModalityType( Dialog.ModalityType.APPLICATION_MODAL);// このWindowを閉じるまで他のWindowの操作禁止
			
			// IDとパスワードの入力がキャンセルされたら，Nullを結果として返し終了
			if( ld.canceled) {
				return	"";
			}
			
			// ユーザIDとパスワードが入力された場合の処理
			reservationUserID	= ld.tfUserID.getText();
			String	password	= ld.tfPassword.getText();
			
			connectDB();												// MySQLに接続
			try {
				// ユーザの情報を取得するクエリ
				String	sql = "SELECT * FROM db_reservation.user WHERE BINARY user_id ='" + reservationUserID + "';";
				System.out.println( sql);				// @@@@ デバッグ用SQLをコンソールに表示
				// クエリを実行して結果セットを取得
				ResultSet	rs	= sqlStmt.executeQuery( sql);
				// パスワードチェック
				if(rs.next()){
					String	password_from_db = rs.getString( "password");// DBに登録されているパスワードを取得
					if( password_from_db.equals( password)) {			// 入力パスワードが正しい時
						flagLogin	= true;								// ログイン済みに設定
						frame.buttonLog.setLabel( "ログアウト");		// ログインボタンの表示をログアウトに変更
						frame.tfLoginID.setText( reservationUserID);	// ログインユーザIDにログイン済みのIDを表示
					} else {											// パスワードが正しくない時
						res = "IDまたはパスワードが違います。";			// 結果表示エリアに表示するメッセージをセット
					}
				} else {												// 非登録ユーザの時
					res = "IDが違います。";								// 結果表示エリアに表示するメッセージをセット
				}
			} catch( Exception e) {										// 例外発生時
				e.printStackTrace();									// StackTraceを表示
			}
			closeDB();													// MySQLの接続を切断
		}
		return	res;
	}
	
	//// @1 教室概要ボタン押下時の処理を行うメソッド
	public	String	getFacilityExplanation( String facility_id) {		// @1
		String		res = "";											// @1 戻り値変数の初期化
		String		exp	= "";											// @1 explanationを入れる変数の宣言
		String		openTime	= "";									// @1 open_timeを入れる変数の宣言
		String		closeTime	= "";									// @1 close_timeを入れる変数の宣言
		connectDB();													// @1 MySQLに接続
		try {															// @1
			String	sql = "SELECT * FROM db_reservation.facility WHERE facility_id = '" + facility_id + "';";	// @1
			ResultSet	rs = sqlStmt.executeQuery( sql);				// @1 選択された教室IDと同じレコードを抽出
			if( rs.next()) {											// @1 1件目のレコードを取得
				exp			= rs.getString( "explanation");				// @1 explanation属性データを取得
				openTime	= rs.getString( "open_time");				// @1 open_time属性データの取得
				closeTime	= rs.getString( "close_time");				// @1 close_time属性データの取得
				// @1 教室概要データの作成
				res = exp + "　利用可能時間：" + openTime.substring( 0,5) + "～" + closeTime.substring( 0,5);	// @1
			} else {													// @1 該当するレコードが無い場合
				res = "教室番号が違います。";							// @1 結果表示エリアに表示する文言をセット
			}															// @1
		} catch( Exception e) {											// @1 例外発生時
			e.printStackTrace();										// @1 StackTraceをコンソールに表示
		}																// @1
		closeDB();														// @1 MySQLの接続を切断
		return	res;													// @1
	}																	// @1
																		// @1
	//// @1 全てのfacility_idを取得するメソッド
	public	ArrayList<String>	getFacilityId() {						// @1
		ArrayList<String> facilityId	= new ArrayList<String>();		// @1 全てのfacilityIDを入れるリストを作成
		connectDB();													// @1 MySQLに接続
		try {															// @1
																		// @1 facilityテーブルの全データを取得するSQL文
			String		sql = "SELECT * FROM db_reservation.facility ORDER BY facility_id DESC;";	// @1 
			ResultSet	rs	= sqlStmt.executeQuery( sql);				// @1 SQL文を送信し，テーブルデータを取得
			while( rs.next()) {											// @1 取得したレコードがなくなるまで繰り返す
				facilityId.add( rs.getString( "facility_id"));			// @1 取り出したレコードのfacility_idをリストに加える
			}															// @1
		} catch( Exception e) {											// @1 例外発生時
			e.printStackTrace();										// @1 StackTraceをコンソールに表示
		}																// @1
		closeDB();														// @1 MySQLを切断
		return	facilityId;												// @1 全てのfacility_idの入ったListを返す
	}																	// @1
	
	//// @2 新規予約ボタン押下時の処理を行うメソッド
	public	String	makeReservation( MainFrame frame) {								// @2
		String	res = "";															// @2 結果を入れる戻り値変数を初期化（Nullを結果）
																					// @2
		if( flagLogin) {															// @2 ログイン済みの場合
			// @2 新規予約画面生成
			ReservationDialog	rd = new ReservationDialog( frame, this);			// @2
																					// @2
			// @2 新規予約画面を表示
			rd.setVisible( true);													// @2 新規予約画面を表示（ここで制御がrdインスタンスに移る）
			if( rd.canceled) {														// @2 新規予約操作をキャンセルしたとき
				return	res;														// @2 新規予約終了
			}																		// @2
			// @2 新規予約操作を正常実行したとき
			// @2 新規予約画面から年月日を取得
			String	ryear_str	= rd.tfYear.getText();								// @2 入力された年情報をテキストで取得
			String	rmonth_str	= rd.tfMonth.getText();								// @2 選択された月情報をテキストで取得
			String	rday_str	= rd.tfDay.getText();								// @2 選択された日情報をテキストで取得
			// @2 月と日が一桁だったら，前に0を付加
			if( rmonth_str.length() == 1) {											// @2 月の文字数が1桁の時
				rmonth_str = "0" + rmonth_str;										// @2 　月の先頭に"0"を付加
			}																		// @2
			if( rday_str.length() == 1) {											// @2 日の文字数が1桁の時
				rday_str = "0" + rday_str;											// @2 　日の先頭に"0"を付加
			}																		// @2
																					// @2
			// @2 入力された日付が正しいか，以下2点をチェック
			// @2   入力された文字が半角数字になっているか．
			// @2   日付として成立している値か
			try {																	// @2
				DateFormat	df = new SimpleDateFormat( "yyyy-MM-dd");				// @2 日付のフォーマットを定義
				df.setLenient( false);												// @2 日付フォーマットのチェックを厳格化
				// @2 入力された日付を文字列に変換したものと，SimpleDateFormatに当てはめて同じ値になるかをチェック
				String	inData = ryear_str + "-" + rmonth_str + "-" + rday_str;		// @2 入力日付を文字列形式でyyyy-MM-dd形式に合成
				String	convData = df.format( df.parse( inData));					// @2 入力日付をSimpleDateFormat形式に変換
				if( !inData.equals( convData)) {									// @2 2つの文字列が等しくない時．
					//res	= "日付の書式を修正して下さい。（年：西暦4桁，月：1～12，日：1～31(各月月末まで))";	// @2 エラー文を設定し，新規予約終了
					res = "全角で入力されています。半角で入力してください。";
					return	res;													// @2
				}																	// @2
			} catch( ParseException p) {											// @2 年月日の文字が誤っていてSimpleDateFormatに変換不可の時
				res = "日付の値を修正して下さい。";									// @2 数字以外，入力されていないことを想定したエラー処理
				return	res;														// @2
			}																		// @2
																					// @2
			// @2 入力された開始日が現時点より後であるかのチェック
			Calendar	dateReservation = Calendar.getInstance();					// @2 
			// @2 入力された予約日付及び現在の日付をCalendarクラスの情報として持つ
			dateReservation.set( Integer.parseInt( ryear_str), Integer.parseInt( rmonth_str)-1, Integer.parseInt( rday_str));	//@2
			Calendar	dateNow = Calendar.getInstance();							// @2 現在日時を取得
																					// @2
			// @2 翌日以降の予約の時
			if( dateReservation.after( dateNow)) {									// @2
				// @2 新規予約画面から教室名,利用開始時刻，終了時刻を取得
				String	facility	= rd.choiceFacility.getSelectedItem();			// @2
				String	st			= rd.startHour.getSelectedItem() + ":" + rd.startMinute.getSelectedItem() + ":00";	// @2
				String	et			= rd.endHour.getSelectedItem() + ":" + rd.endMinute.getSelectedItem() + ":00";		// @2
																					// @2
				// @2 開始時刻と終了時刻が同じ時
				if( st.compareTo( et) >= 0) {										// @2
					res	= "開始時刻と終了時刻が同じか終了時刻の方が早くなっています。";// @2
				} else {															// @2
					// @2 開始時刻と終了時刻は同じではない時
					try{															// @2
						// @2 選択されている時間が利用可能時間の範囲か
						int	limit[][] = getAvailableTime( facility);				// @2 選択教室の利用可能な開始時刻と終了時刻を取得
						String	limitString[][] = {{ "", ""},{ "", ""}};			// @2 　開始時分，終了時分を文字列で持つための変数定義
						for( int i = 0; i<2; i++) {									// @2
							for( int j=0; j<2; j++) {								// @2
								limitString[i][j] = String.valueOf(limit[i][j]);	// @2 　開始時分，終了時分を文字列に変換
								if( limitString[i][j].length() ==1) {				// @2 　開始時分，終了時分が1桁なら先頭に0を付加
									limitString[i][j] = "0" + limitString[i][j];	// @2
								}													// @2
							}														// @2
						}															// @2
						String	startLimit	= limitString[0][0] + ":" + limitString[0][1] + ":00";	// @2 利用可能開始時分を文字列に合成
						String	endLimit	= limitString[1][0] + ":" + limitString[1][1] + ":00";	// @2 利用可能終了時分を文字列に合成
						// @2 入力された利用開始・終了時間が教室の利用可能開始時前もしくは利用可能終了時後の時
						if( startLimit.compareTo( st) > 0 || endLimit.compareTo( et) < 0) {			// @2
							res = "利用可能時間外です。";								// @2
						// @2 開始時間及び終了時間が利用可能な範囲の時
						} else {													// @2
							// @2 指定された時間で予約可能かどうかのチェック
							connectDB();											// @2 MySQLに接続
							// @2 月日が1桁なら前に0を付ける
							if( rmonth_str.length() == 1) {							// @2
								rmonth_str = "0" + rmonth_str;						// @2
							}														// @2
							if( rday_str.length() == 1) {							// @2
								rday_str = "0" + rday_str;							// @2
							}														// @2
							// @2 reservationテーブルより，新規予約日の予約情報を取得する
							String	rdate = ryear_str + "-" + rmonth_str + "-" + rday_str;			// @2
							// @2 指定教室の新規予約日の予約情報を取得するクエリ作成
							String	sql = "SELECT * FROM db_reservation.reservation WHERE facility_id = '" + facility + "' AND day = '" + rdate + "';";	// @2
							System.out.println( sql);				// @@@@ デバッグ用SQLをコンソールに表示
							ResultSet	rs = sqlStmt.executeQuery( sql);			// @2 
							// @2 検索結果に対して時間の重なりをチェック
							boolean	ng = false;										// @2 結果の初期値をチェックOKに設定
							// @2 取得したタプルを1件ずつチェック
							while( rs.next()) {										// @2
								// @2 タプルの開始時刻，終了時刻をそれぞれstartとendに設定
								String	start	= rs.getString("start_time");		// @2
								String	end		= rs.getString("end_time");			// @2
								// @2 予約済みの開始時刻が新規予約の開始時刻以前で，新規予約の開始時刻が予約済みの終了時刻以前か
								// @2 新規予約の開始時刻が予約済みの開始時刻以前で，予約済みの開始時刻が新規予約の終了時刻以前ならば
								// @2 重複ありと判定
								if(( start.compareTo( st) <= 0 && st.compareTo( end) <= 0) ||	// @2
								   ( st.compareTo( start) <= 0 && start.compareTo( et) <= 0)) {	// @2
									ng = true;										// @2
									break;											// @2
								}													// @2
							}														// @2

							// @2 予約済みと重なりがない場合
							if( !ng) {												// @2
								Calendar	justNow = Calendar.getInstance();		// @2 予約した日時を取得
								SimpleDateFormat	resDate = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss");//@2 予約した日時のフォーマットを定義
								String	now = resDate.format( justNow.getTime());	// @2 予約した日時（現在日時）データを取得
																					// @2
								// @2 予約情報をreservatonテーブルに登録する
								sql = "INSERT INTO db_reservation.reservation( facility_id, user_id, date, day, start_time, end_time) VALUES( '"
										+ facility + "','" + reservationUserID + "','" + now + "','" + rdate + "','" + st + "','" + et + "');";
								System.out.println( sql);				// @@@@2 デバッグ用SQLをコンソールに表示
								sqlStmt.executeUpdate( sql);						// @2 SQL文をMySQLに投げる
																					// @2 予約完了表示作成
								res = rdate + " " + st.substring( 0, 5) + "～" + et.substring( 0, 5) + " " + facility + "教室を予約しました。";// @2
							// @2 登録されている予約情報と重なりがある場合
							} else {												// @2
								res = "既にある予約に重なっています。";				// @2
							}														// @2
						}															// @2
					// @2 途中で予期しない例外が発生した場合
					} catch( Exception e) {											// @2
						res = "予期しないエラーが発生しました。";					// @2
						e.printStackTrace();										// @2 StackTraceをコンソールに表示
					}																// @2
					closeDB();														// @2 MySQLとの接続を切る
				}																	// @2
			// @2 予約日が当日かそれより前だった場合
			} else {																// @2
				res = "予約日が無効です。";											// @2
			}																		// @2
		// @2 未ログイン状態の場合
		} else {																	// @2
			res = "ログインして下さい。";											   // @2
		}																			// @2
		return res;																	// @2
	}																				// @2
	
	//// @2 指定教室の利用可能開始・終了時間を取得する
	//// @2	（戻り値：abailableTime[][]={{ 開始時, 開始分}, { 終了時, 終了分}}
	public	int[][] getAvailableTime( String facility) {							// @2
		int [][] abailableTime = {{ 0, 0}, { 0, 0}};								// @2 開始時・終了時をこの配列に入れて呼び元に返す
		connectDB();																// @2 MySQLに接続
		try {																		// @2
			String		sql = "SELECT * FROM db_reservation.facility WHERE facility_id = " + facility + ";";	// @2
			ResultSet	rs	= sqlStmt.executeQuery( sql);							// @2 選択された教室IDのタプルを取得
			while( rs.next()) {														// @2 タプルが無くなるまで繰り返す
				String	timeData = rs.getString( "open_time");						// @2 タプルのopen_timeを取得
				abailableTime[0][0] = Integer.parseInt( timeData.substring( 0, 2));	// @2 open_timeの「時」を整数型に変換
				abailableTime[0][1] = Integer.parseInt( timeData.substring( 3, 5));	// @2 open_timeの「分」を整数型に変換
				timeData = rs.getString( "close_time");								// @2 タプルのclose_timeを取得
				abailableTime[1][0] = Integer.parseInt( timeData.substring( 0, 2));	// @2 close_timeの「時」を整数型に変換
				abailableTime[1][1] = Integer.parseInt( timeData.substring( 3, 5));	// @2 close_timeの「分」を整数型に変換
			}																		// @2
		} catch( Exception e) {														// @2 該当するレコードがない，「時」や「分」を変換出来ないなど
			e.printStackTrace();													// @2 StackTraceをコンソールに表示
		}																			// @2
		return	abailableTime;														// @2 open_time,close_timeの「時」を返す（エラーなら{0,0}が返る
	}
	
////@3 予約確認ボタン押下時の処理を行うメソッド
	public	String	getReservation( MainFrame frame) {		
		String	res 			= "";								// @3 戻り値変数の初期化
//		String  reservationID 	= "";								// @3 reservation_idを入れる変数の宣言
//		String  facilityID 		= "";								// @3 facility_idを入れる変数の宣言
//		String  userID 			= "";								// @3 user_idを入れる変数の宣言
//		String  date 			= "";								// @3 dateを入れる変数の宣言
//		String  day 			= "";								// @3 dayを入れる変数の宣言
//		String  startTime 		= "";								// @3 start_timeを入れる変数の宣言
//		String  endTime 		= "";								// @3 end_timeを入れる変数の宣言

		// @3 予約確認画面生成
		GetReservationDialog	rd = new GetReservationDialog( frame, this);
																			
		// @3 予約確認画面を表示
		rd.setVisible( true);													// @3 予約確認画面を表示（ここで制御がrdインスタンスに移る）
		if( rd.canceled) {														// @3 予約確認操作をキャンセルしたとき
			return	res;														// @3 予約確認終了
		}								

		// @3 予約確認操作を正常実行したとき
		// @3 予約確認画面から年月日を取得
		String	ryear_str	= rd.tfYear.getText();								// @3 入力された年情報をテキストで取得
		String	rmonth_str	= rd.tfMonth.getText();								// @3 選択された月情報をテキストで取得
		String	rday_str	= rd.tfDay.getText();								// @3 選択された日情報をテキストで取得
		// @3 月と日が一桁だったら，前に0を付加
		if( rmonth_str.length() == 1) {											// @3 月の文字数が1桁の時
			rmonth_str = "0" + rmonth_str;										// @3 　月の先頭に"0"を付加
		}																		// @3
		if( rday_str.length() == 1) {											// @3 日の文字数が1桁の時
			rday_str = "0" + rday_str;											// @3 　日の先頭に"0"を付加
		}																		// @3
																				// @3
		// @3 入力された日付が正しいか，以下2点をチェック
		// @3   入力された文字が半角数字になっているか．
		// @3   日付として成立している値か
		try {																	// @3
			DateFormat	df = new SimpleDateFormat( "yyyy-MM-dd");				// @3 日付のフォーマットを定義
			df.setLenient( false);												// @3 日付フォーマットのチェックを厳格化
			// @3 入力された日付を文字列に変換したものと，SimpleDateFormatに当てはめて同じ値になるかをチェック
			String	inData = ryear_str + "-" + rmonth_str + "-" + rday_str;		// @3 入力日付を文字列形式でyyyy-MM-dd形式に合成
			String	convData = df.format( df.parse( inData));					// @3 入力日付をSimpleDateFormat形式に変換
			if( !inData.equals( convData)) {									// @3 2つの文字列が等しくない時．
				//res	= "日付の書式を修正して下さい。（年：西暦4桁，月：1～12，日：1～31(各月月末まで))";	// @3 エラー文を設定し，新規予約終了
				res = "全角で入力されています。半角で入力してください。";
				return	res;													// @3
			}																	// @3
		} catch( ParseException p) {											// @3 年月日の文字が誤っていてSimpleDateFormatに変換不可の時
			res = "日付の値を修正して下さい。";						                // @3 数字以外，入力されていないことを想定したエラー処理
			return	res;														// @3
		}																		// @3
																				// @3
		// @3 予約確認画面から教室名を取得
		String	facility	= rd.choiceFacility.getSelectedItem();	// @3
		String	rdate = ryear_str + "-" + rmonth_str + "-" + rday_str;
		connectDB();													// @3 MySQLに接続
		try {															// @3
			String	sql = "SELECT * FROM db_reservation.reservation WHERE facility_id = '" + facility + "' AND day = '" + rdate + "' ORDER BY start_time;";	// @1
			ResultSet	rs = sqlStmt.executeQuery( sql);				// @3 選択された教室IDと同じレコードを抽出
			if( rs.next()) {											// @3 1件目のレコードを取得
//				String reservationID	= rs.getString( "reservation_id");		// @3 reservation_id属性データを取得
//				String facilityID = rs.getString( "facility_id");				// @3 facility_id属性データの取得
//				String userID	= rs.getString( "user_id");						// @3 user_id属性データの取得
				String date = rs.getString( "date");							// @3 date属性データの取得
//				String day = rs.getString("day");								// @3 day属性データの取得
				String startTime = rs.getString("start_time");					// @3 start_time属性データの取得
				String endTime = rs.getString("end_time");						// @3 end_time属性データの取得
				// @3 結果表示エリアに表示する文言をセット(変数を使用する場合)
//				res = "予約番号:" +reservationID + "  教室番号：" + facilityID + "  ユーザーID：" + userID + "  予約実行時間：" + date + "  予約日：" + day + "  利用可能時間：" + startTime.substring( 0,5) + "～" + endTime.substring( 0,5);	// @1
				// @3 結果表示エリアに表示する文言をセット(直接ResultSetから取得した値を使用する場合)
				res	= "予約番号:" + rs.getString( "reservation_id") + "  教室番号：" + rs.getString( "facility_id") + "  予約実行時間：" + date.substring( 0,19) 
				+ "  予約日：" + rs.getString( "day") + "  利用時間：" + startTime.substring( 0,5) + "～" + endTime.substring( 0,5) + "\n";
				while( rs.next()) {
					// @3 予約が二つ以上ある場合の結果表示エリアに表示する文言をセット
					date = rs.getString( "date");							// @3 date属性データの取得
					startTime = rs.getString("start_time");					// @3 start_time属性データの取得
					endTime = rs.getString("end_time");						// @3 end_time属性データの取得
					res	+= "予約番号:" + rs.getString( "reservation_id") + "  教室番号：" + rs.getString( "facility_id") + "  予約実行時間：" + date.substring( 0,19) 
					+ "  予約日：" + rs.getString( "day") + "  利用時間：" + startTime.substring( 0,5) + "～" + endTime.substring( 0,5) + "\n";
				}
			} else {													// @3 該当するレコードが無い場合
				res = "予約はありません。";								   // @3 結果表示エリアに表示する文言をセット
			}															// @3
		} catch( Exception e) {											// @3 例外発生時
			res = "予期しないエラーが発生しました。";
			e.printStackTrace();										// @3 StackTraceをコンソールに表示
		}																// @3
		closeDB();														// @3 MySQLとの接続を切る
	    return res;														// @3 結果表示内容をMainFrameに返す。
	}
	
//// @4 自己予約確認ボタン押下時の処理を行うメソッド
	public String getMyReservation( MainFrame frame) {
		String res = "";												// @4 結果を入れる戻り値変数を初期化
		
		if(flagLogin) {													// @4 ログイン状態を確認
			// @4 ログイン済みの場合
			connectDB();												// @4 MySQLに接続
			try {
				// System.out.println(LocalDate.now());					// @4 今日の日付をコンソールに表示（テスト用）
				// @4 sql文作成 条件文：user_idが合致、かつ、予約日が今日以降
				String sql = "SELECT * FROM db_reservation.reservation WHERE user_id = '" + reservationUserID + "' AND day >= '" + LocalDate.now() +"' ORDER BY day, start_time;";
				ResultSet	rs = sqlStmt.executeQuery( sql);			// @4 MySQLに送信
				if(rs.next()) {											// @4 １件目のレコードを取得
					// @4 結果表示エリアに表示する文言をセット
					res	= "予約番号:" + rs.getString( "reservation_id") + "  教室番号：" + rs.getString( "facility_id") + "  ユーザID：" + rs.getString( "user_id") 
						+ "  予約実行時間：" + rs.getString( "date").substring( 0,19) + "  予約日：" + rs.getString( "day") + "  利用時間：" + rs.getString("start_time").substring( 0,5) + "～" + rs.getString( "end_time").substring( 0,5) + "\n";
					while( rs.next()) {
						// @4 予約が二つ以上ある場合の結果表示エリアに表示する文言をセット
						res	+= "予約番号:" + rs.getString( "reservation_id") + "  教室番号：" + rs.getString( "facility_id") + "  ユーザID：" + rs.getString( "user_id") 
							+ "  予約実行時間：" + rs.getString( "date").substring( 0,19) + "  予約日：" + rs.getString( "day") + "  利用時間：" + rs.getString("start_time").substring( 0,5) + "～" + rs.getString( "end_time").substring( 0,5) + "\n";
					}
				} else {
					// @4 現在以降の予約がなかった場合
					res = "現在以降の予約はありません。";
				}
			} catch (Exception e) {										// @4 例外発生時
				res = "予期しないエラーが発生しました。";				// @4 StackTraceをコンソールに表示
				e.printStackTrace();
			}
		}else {
			// @4 未ログイン時
			res = "ログインして下さい。";
		}
		closeDB();														// @4 MySQLとの接続を切る
		return res;														// @4 結果表示内容をMainFrameに返す。
	}
	
//// @5 予約キャンセルボタン押下時の処理を行うメソッド
	public String cancelReservation( MainFrame frame) {
		String res = "";												// @5 結果を入れる戻り値変数を初期化
		ArrayList<String> reservationId = new ArrayList<String>();		// @5 予約キャンセル画面の予約選択コンボボックス用List変数を初期化
		if(flagLogin) {													// @5 ログイン状態を確認
			// @5 ログイン状態の場合
			connectDB();												// @5 MySQLに接続
			try {
				// @5 ユーザに削除できる予約があるかどうか調べる
				// @5 ユーザが予約している予約情報を取得するクエリ作成
				String sql = "SELECT * FROM db_reservation.reservation WHERE user_id = '" + reservationUserID + "' AND day > '" + LocalDate.now() + "' ORDER BY day, start_time;";
				ResultSet	rs	= sqlStmt.executeQuery( sql);
				if(rs.next()) {
					reservationId.add(rs.getString( "reservation_id"));		// @5 キャンセル可能の予約番号をリストに格納
					while( rs.next()) {
						reservationId.add(rs.getString( "reservation_id"));		// @5 複数ある場合の二つ目以降予約番号をリストに格納
					}
					// @5 予約キャンセル画面生成
					CancelReservationDialog	rd = new CancelReservationDialog(frame, this, reservationId);
					// @5 予約キャンセル画面を表示
					rd.setVisible( true);
					if(rd.canceled) {
						return res;
					}
					// @5 選択された予約情報をテキストで取得
					String	cancelReservationId_str	= rd.choiceReservationId.getSelectedItem();
					try {
						// @5 削除する予約情報を結果表示用に確保しておく
						// @5 選択された予約番号の予約情報を取得するクエリを作成
						sql	= "SELECT * FROM db_reservation.reservation WHERE reservation_id = '" 
										+ cancelReservationId_str + "';";
						rs = sqlStmt.executeQuery( sql);				// @5 MySQLに送信
						if(rs.next()) {
						// @5 結果表示エリアに表示する文言をセット
						res = "キャンセルした予約情報[ 予約番号:" + rs.getString( "reservation_id") + "  教室番号：" + rs.getString( "facility_id") 
								+ "  ユーザID：" + rs.getString( "user_id") + "  予約実行時間：" + rs.getString( "date").substring( 0,19) 
								+ "  予約日：" + rs.getString( "day") + "  利用時間：" + rs.getString("start_time").substring( 0,5) + "～" 
								+ rs.getString( "end_time").substring( 0,5) + " ]";					
						}
						rs.next();
						// @5 選択された予約番号をもとに予約情報を削除する
						// @5 選択された予約番号の予約情報を削除するクエリを作成
						sql = "DELETE FROM db_reservation.reservation WHERE reservation_id = '" + cancelReservationId_str +"';";
						System.out.println( sql);						// @@@@5 デバッグ用SQLをコンソールに表示
						sqlStmt.executeUpdate( sql);					// @5 SQL文をMySQLに投げる
						// @5 削除完了
					} catch (Exception e) {								// @5 例外発生時
						res = "予期しないエラーが発生しました。";		// @5 
						e.printStackTrace();							// @5 StackTraceをコンソールに表示
					}
				} else {
					// @5 削除できる予約がない場合
					res = "キャンセルできる予約がありません。";
				}
			} catch (Exception e) {
				res = "予期しないエラーが発生しました。";
			}
			closeDB();													// @5 MySQLとの接続を切る
		}else {
			// @5 未ログイン状態の場合
			res = "ログインして下さい。";
		}
		return res;														// @5 結果表示内容をMainFrameに返す。
	}
// @5 メソッド呼出から引数で渡すことにしたため、不要になったメソッド
////// @5 予約キャンセル機能用のキャンセルできる予約の予約番号をListで取得して戻り値で返すメソッド
//	public ArrayList<String> getCancelPossibleReservationId() {
//		ArrayList<String> reservationId = new ArrayList<String>();		// @5 コンボボックス用予約番号を入れる戻り値変数を初期化
//		connectDB();													// @5 MySQLに接続
//		try {
//			// @5 user_idが合致、かつ、予約日が翌日以降の予約情報を取得するクエリを作成
//			String sql = "SELECT * FROM db_reservation.reservation WHERE user_id = '" + reservationUserID + "' AND day > '" + LocalDate.now() + "' ORDER BY day, start_time;";
//			ResultSet	rs = sqlStmt.executeQuery( sql);
//			while( rs.next()) {
//				reservationId.add(rs.getString( "reservation_id"));		// @5 予約番号をリストに格納
//			}
//		} catch (Exception e) {											// @5 例外発生時
//			e.printStackTrace();										// @5 StackTraceをコンソールに表示
//		}
//		closeDB();														// @5 MySQLとの接続を切る
//		return reservationId;											// @5 予約番号のリストをCanselReservationDialigに返す
//	}
	
//// @5 予約キャンセル画面の実行ボタン押下時の処理を行うメソッド
	public boolean confirmCancelReservation( CancelReservationDialog dialog, String selectReservationId) {
		String selectReservation = "";									// @5 表示用予約情報を入れる戻り値変数を初期化
		connectDB();													// @5 MySQLに接続
		try {
			// @5 選択された予約番号の予約情報を取得するクエリを作成
			String sql	= "SELECT * FROM db_reservation.reservation WHERE reservation_id = '" 
							+ selectReservationId + "';";
			ResultSet rs = sqlStmt.executeQuery( sql);					// @5 MySQLに送信
			if( rs.next()) {
				// @5 予約キャンセル確認画面に表示する文言をセット
				selectReservation = "予約番号：" + rs.getString( "reservation_id") + "　教室番号：" + rs.getString( "facility_id") + "　予約日："
									+ rs.getString( "day") + "　利用時間:" + rs.getString( "start_time").substring( 0,5) + "～" + rs.getString( "end_time").substring( 0,5) ;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// @5 予約キャンセル確認画面を生成
		ConfirmCancelReservationDialog rd = new ConfirmCancelReservationDialog(dialog, this, selectReservation);
		// @5 予約キャンセル確認画面を表示
		rd.setVisible( true);
		// @5 実行ボタン押下：false　キャンセルボタン押下：true
		if(rd.canceld) {
			// @5 キャンセルボタン押下時にfalseを戻り値で返す。
			return false;										
		}
		// @5 実行ボタン押下時にtureを戻り値で返す。
		return true;
	}
}
