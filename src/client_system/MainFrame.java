package client_system;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class MainFrame extends Frame implements ActionListener, WindowListener{
	ReservationControl	reservationControl;						// ReservationControlクラスのインスタンス生成
	// パネルインスタンスの生成
	Panel	panelNorth;											// 上部パネル
	Panel	panelCenter;										// 中央パネル
	// ボタンインスタンスの生成
	Button	buttonLog;											// ログイン・ログアウトボタン
	// テキストフィールドのインスタンス生成
	TextField	tfLoginID;										// ログインIDを表示するテキストフィールド
	// テキストエリアのインスタンス生成
	TextArea	textMessage;									// 結果表示用メッセージ欄
	
	//// MainFrameコンストラクタ
	public	MainFrame( ReservationControl rc) {
		reservationControl	= rc;								// ReservationControlインスタンス保存

		// ボタンの生成
		buttonLog = new Button( " ログイン ");					// ログインボタン

		// ログインID用表示ボックスの生成
		tfLoginID = new TextField( "未ログイン", 10);
		tfLoginID.setEditable( false);
		
		// 上中下のパネルを使うため，レイアウトマネージャーにBorderLayoutを設定
		setLayout( new BorderLayout());
		
		// 上部パネルに部品を配置
		panelNorth = new Panel();								// 上部パネルインスタンスを生成
		panelNorth.add( new Label( "教室予約システム"));		// タイトルラベルを付加
		panelNorth.add( buttonLog);								// ログインボタンを付加
		panelNorth.add( new Label( "　　　　　　ログインID:"));	// ログインIDラベルを付加
		panelNorth.add( tfLoginID);								// ログインID表示テキストフィールドを付加
		
		// MainFrameに上部パネルを追加
		add( panelNorth, BorderLayout.NORTH);

		// 中央パネルにテキストメッセージ欄を設定
		panelCenter = new Panel();								// 中央パネルインスタンスを生成
		textMessage = new TextArea( 20, 80);					// メッセージ表示用テキストエリアインスタンスを生成
		textMessage.setEditable( false);						// テキストエリアをユーザによる編集不可に設定
		panelCenter.add( textMessage);							// 中央パネルにメッセージ表示エリアを付加
		// MainFrameに中央パネルを追加
		add( panelCenter, BorderLayout.CENTER);
		
		// Listenerの追加
		buttonLog.addActionListener( this);						// ActionListenerにログインボタンを追加
		addWindowListener( this);								// WindowListenerを追加
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		System.exit( 0);
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String	result = new String();
		if( e.getSource() == buttonLog) {						// 押下ボタンがログインボタンの時
			result = reservationControl.loginLogout( this);		// loginLogoutメソッドを実行
		}
		textMessage.setText( result);							// loginLogoutメソッドの戻り値をテキストエリアに表示
	}

}
