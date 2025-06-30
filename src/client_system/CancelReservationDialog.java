package client_system;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;

public class CancelReservationDialog extends Dialog implements ActionListener, WindowListener{

	boolean	canceled;							// 新規予約キャンセルステータス（キャンセル：true）
	ReservationControl	rc;						// ReservationControlインスタンス保存用
	
	// パネル
	Panel	panelNorth;
	Panel	panelSouth;
	
	// 入力用コンポーネント
	ChoiceFacility choiceReservationId;			// 予約ID選択用ボックス
	
	// ボタン
	Button buttonOK;							// 削除実行ボタン
	Button buttonCancel;						// キャンセルボタン
	
	
	public CancelReservationDialog ( Frame owner, ReservationControl rc) {
		// 基底クラスのコンストラクタを呼び出す
		super( owner, "予約キャンセル", true);
		
		this.rc = rc;							// ReservationControlのインスタンスを保存
		// 初期値キャンセルを設定
		canceled = true;
		
		List<String> reservationId 	= new ArrayList<String>();		// キャンセルできる予約の情報を入れるList
		reservationId 	= rc.getCancelPossibleReservationId();		// キャンセルできる予約の情報を読み出し、Listに格納
		choiceReservationId = new ChoiceFacility(reservationId);	// キャンセルする予約選択用コンボボックスのインスタンス生成
		
//		System.out.println(reservationId);							// テスト用プリント文
		
		// ボタンの生成
		buttonOK = new Button("実行");
		buttonCancel = new Button("キャンセル");
		
		// パネルの生成
		panelNorth	= new Panel();
		panelSouth	= new Panel();
		
		// 上部パネルに予約番号選択コンボボックス追加
		panelNorth.add( new Label("キャンセルしたい予約を選択　予約番号："));
		panelNorth.add( choiceReservationId);
		
		// 下部パネルにキャンセルボタンと実行ボタンを追加
		panelSouth.add( buttonCancel);
		panelSouth.add( new Label("　"));
		panelSouth.add( buttonOK);
		
		// ReservationDialogをBorderLayoutに設定し，2つのパネルを追加
		setLayout( new BorderLayout());
		add( panelNorth,	BorderLayout.NORTH);
		add( panelSouth,	BorderLayout.SOUTH);
		
		// Window Listenerを追加
		addWindowListener( this);
		// ボタンにアクションリスナを追加
		buttonOK.addActionListener( this);
		buttonCancel.addActionListener( this);
		
		// 大きさの設定，ウィンドウのサイズ変更不可の設定
		this.setBounds( 100, 200, 500, 150);
		setResizable( false);
	}
	
	
	@Override
	public void windowOpened(WindowEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		
	}
	@Override
	public void windowClosing(WindowEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		setVisible( false);
		dispose();	
		
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
		// TODO 自動生成されたメソッド・スタブ
		if( e.getSource() == buttonCancel) {
			setVisible( false);
			dispose();
		} else if( e.getSource() == buttonOK) {
			// 実行ボタン押下時、reservationControlのconfirmCancelReservationを呼び出す。
			// 引数でコンボボックスで選択している予約番号を渡す。戻り値型はboolean
			if(rc.confirmCancelReservation(null, choiceReservationId.getSelectedItem())) {
				// 予約キャンセル確認画面で実行ボタン押下時、canceledをfalseにして、ダイアログを閉じる。
				canceled = false;
				setVisible( false);
				dispose();
			}
		}
	}
	
	

}
