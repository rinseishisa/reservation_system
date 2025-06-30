package client_system;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class ConfirmCancelReservationDialog extends Dialog implements ActionListener, WindowListener{

	boolean canceld;
	ReservationControl rc;
	
	Panel	panelNorth;
	Panel	panelCenter;
	Panel	panelSouth;
	
	Button	buttonOK;
	Button	buttonCancel;
	
	public ConfirmCancelReservationDialog( Dialog owner, ReservationControl rc, String selectReservation) {
		// 基底クラスのコンストラクタを呼び出す
		super( owner, "予約キャンセル確認", true);
		// ReservationControlのインスタンスを保存
		this.rc = rc;
		// 初期値キャンセルを設定
		canceld = true;
		
		// ボタンの作成
		buttonOK = new Button("実行");
		buttonCancel = new Button("キャンセル");
		
		// パネル追加
		panelNorth	= new Panel();
		panelCenter	= new Panel();
		panelSouth	= new Panel();
		
		// 上部パネルにラベルを追加
		panelNorth.add( new Label( "キャンセル対象 [ " + selectReservation + " ]"));
		
		// 中央パネルにラベルを追加
		panelCenter.add( new Label("キャンセルを実行しますか？"));
		
		// 下部パネルに2つのボタンを追加
		panelSouth.add( buttonCancel);
		panelSouth.add( new Label("　"));
		panelSouth.add( buttonOK);
		
		// ConfirmCancelReservationDialogをBorderLayoutに設定し、2つのパネルを追加
		setLayout( new BorderLayout());
		add( panelNorth, 	BorderLayout.NORTH);
		add( panelCenter, 	BorderLayout.CENTER);
		add( panelSouth,	BorderLayout.SOUTH);		
		
		// WindowListenerを追加
		addWindowListener( this);
		// ボタンにアクションリスナを追加
		buttonOK.addActionListener( this);
		buttonCancel.addActionListener( this);
		
		// 大きさの設定、ウィンドウのサイズ変更不可の設定
		this.setBounds( 50, 300, 600, 150);
		setResizable( false);
	}
	
	
	@Override
	public void windowOpened(WindowEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		setVisible( false);
		dispose();	
		
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
			canceld = false;
			setVisible( false);
			dispose();
		}
		
	}

}
