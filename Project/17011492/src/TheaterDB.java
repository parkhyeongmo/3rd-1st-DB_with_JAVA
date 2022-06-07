import java.sql.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

class memberF extends JFrame implements ActionListener, MouseListener{ // ȸ�� ���ý� ��µǴ� ������
	
	Connection con; // SQL Connection �� ���� ���� ��� ���
	ResultSet rs;
	
	int memberId;
	int selectedId = 0;	
	String today = "2021-03-10";

	JOptionPane msg = new JOptionPane(); // �˾� �޼��� ��¿� JOptionPane
	
	JTextField inputTitle = new JTextField(12); // �˻��� �Է� ��
	JTextField inputDirector = new JTextField(12);
	JTextField inputActor = new JTextField(12);
	JTextField inputGenre = new JTextField(12);
	JTextField selectedMovie = new JTextField(10);
	JButton btn_movies = new JButton("��ȸ"); // �˻� ��ư
	JButton btn_reserve = new JButton("����"); // ���� ��ư
	JButton btn_inquiry = new JButton("���� ��ȸ"); // ���� ��ȸ ��ư
	
	String[] movies_property = {"MovieId", "��ȭ��", "�󿵽ð�", "�󿵵��", "������", "����", "�帣", "��ȭ�Ұ�", "������"}; 
	DefaultTableModel model = new DefaultTableModel(movies_property, 0) {		// JTable�� ����� Model
		@Override
		public boolean isCellEditable(int row, int column) { // ���� ��� ����
			return false;
		}
	};
	
	JTable resultTable;
	
	public memberF(Connection con, int memberId) { // ������ �� �� ����
		
		this.con = con;
		this.memberId = memberId;
		
		setTitle("ȸ��");
		
		Container c = getContentPane();
		c.setLayout(new FlowLayout());
				
		JPanel inputForm = new JPanel();
		
		inputForm.add(new JLabel("��ȭ��"));
		inputForm.add(inputTitle);
		inputForm.add(new JLabel("    ������"));
		inputForm.add(inputDirector);
		inputForm.add(new JLabel("    ����"));
		inputForm.add(inputActor);
		inputForm.add(new JLabel("    �帣"));
		inputForm.add(inputGenre);
		inputForm.add(btn_movies);		
		
		JPanel reservePan = new JPanel();
		selectedMovie.setEditable(false); // ���õ� ��ȭ ���� ��� �� ���� ��� ����		
		
		reservePan.add(btn_reserve);
		reservePan.add(btn_inquiry);
		reservePan.add(new JLabel(" ȸ�� ID: "+memberId));
		reservePan.add(new JLabel(" /  ���� ��¥: "+today));
		reservePan.add(new JLabel(" / ���õ� ��ȭ "));
		reservePan.add(selectedMovie);

		resultTable = new JTable(model);
		JScrollPane rsSP = new JScrollPane(resultTable);
		rsSP.setPreferredSize(new Dimension(850, 300));		
		
		c.add(inputForm);
		c.add(reservePan);
		c.add(rsSP);
				
		btn_movies.addActionListener(this); // ��ȸ ��ư
		btn_reserve.addActionListener(this); // ���� ��ư
		btn_inquiry.addActionListener(this); // ���� ��ȸ ��ư
		
		resultTable.addMouseListener(this); // ��ȸ ��� Ŭ�� �� �̺�Ʈ ����
		
		setSize(900, 450);
		setVisible(true);
		
	}
	
	public void actionPerformed(ActionEvent e) { // ��ư Ŭ���� ���� ����
		
		if(e.getSource() == btn_movies) { // ��ȭ �˻� ��ȸ
			select_movie();
		}
		
		else if(e.getSource() == btn_reserve) { // ���� ��ư
			
			if(selectedId == 0) { // ��ȭ�� �������� �ʾ��� �� �˸�â ���
				msg.showMessageDialog(null, "��ȭ�� �������ּ���.");
				return;
			}
			
			Reserve reserve = new Reserve(con, selectedId, memberId, today); // ���� �� ȣ��
		}
		
		else if(e.getSource() == btn_inquiry) {
			myReservation myreserve = new myReservation(con, memberId, today); // ���� ���� ���� �� ȣ��
		}
		
	}
	
	@Override
	public void mouseClicked(MouseEvent e) { // JTable Ŭ�� �� ���� �ֽ�ȭ
		int row = resultTable.getSelectedRow();
		selectedId = (int) resultTable.getModel().getValueAt(row, 0);	
		selectedMovie.setText(resultTable.getModel().getValueAt(row, 1).toString());
	}	
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}	
	@Override
	public void mouseExited(MouseEvent e) {}
	
	private void select_movie() { // �˻� ��ȸ ���

		JOptionPane msg = new JOptionPane();
		String query = "";
		boolean notFirst = false;
		model.setNumRows(0);
		
		try {
			
			query = "Select * from Movies where "; // ������ �ʱ�ȭ
			
			if(!inputTitle.getText().equals("")) {
				query += "title LIKE '%" + inputTitle.getText() + "%'";
				notFirst = true;
			}
			if(!inputDirector.getText().equals("")) {
				if(notFirst)
					query += " and ";
				query += "director LIKE '%" + inputDirector.getText() + "%'";
				notFirst = true;
			}
			if(!inputActor.getText().equals("")) {
				if(notFirst)
					query += " and ";
				query += "actor LIKE '%" + inputActor.getText() + "%'";
				notFirst = true;
			}
			if(!inputGenre.getText().equals("")) {
				if(notFirst)
					query += " and ";
				query += "genre LIKE '%" + inputGenre.getText() + "%'";
				notFirst = true;
			}			
			
			query += ";";
			
			Statement stmt = con.createStatement();
			rs = stmt.executeQuery(query); // ������ ���� �� ����� ��ȯ�޾� ����
			
			while(rs.next()) { // JTable�� ����
				
				Vector factor = new Vector();
				factor.add(rs.getInt(1));
				factor.add(rs.getString(2));
				factor.add(rs.getString(3));
				factor.add(rs.getInt(4));
				factor.add(rs.getString(5));
				factor.add(rs.getString(6));
				factor.add(rs.getString(7));
				factor.add(rs.getString(8));
				factor.add(rs.getString(9));	
				
				model.addRow(factor);
				
			}			
			
		}catch(SQLException exception) {
			msg.showMessageDialog(null, "�˻� Ű���带 �Է����ּ���.");
		}
		
	}	
	
}

class Reserve extends JFrame implements ActionListener, MouseListener{ // ���� ��
	
	Connection con;
	JOptionPane msg = new JOptionPane(); 
	
	String today;
	String screenDay;
	int memberId;
	int movieId;
	int scheduleId;
	int hallId;
	int sellingPrice = 12000;
	
	String[] method = {"ī��", "����"};
	String[] DC = {"ī�� ����", "��Ż� ����", "����"};
	JComboBox<String> Payment = new JComboBox<String>(method);
	JComboBox<String> discount = new JComboBox<String>(DC);
	JButton btn_reserve = new JButton("����");
	JButton btn_cancel = new JButton("���");
	JTextField selectedSched = new JTextField(10); // ������ �� ������ ����� ��
	
	String[] schedules_property = {"ScheduleId", "�󿵰�", "��¥", "����","�ð�"}; 
	DefaultTableModel model = new DefaultTableModel(schedules_property, 0) { // ������ ��ȭ�� �� ������ ������ model		
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};
	
	JTable schedulesTable = new JTable(model);
	
	public Reserve(Connection con, int movieId, int memberId, String today) { // ������ �� �� ����
		
		this.con = con;
		this.memberId = memberId; 
		this.movieId = movieId;
		this.today = today;
		
		setTitle("����");
		
		Container c = getContentPane();
		c.setLayout(new FlowLayout());
		
		selectedSched.setEditable(false); // ���õ� �� ���� ���â�� ���� ��� ����
		
		JPanel selectPanel = new JPanel(); // ������Ʈ���� �׷�ȭ�� JPanel ����
		JPanel btnPanel = new JPanel();
		
		selectPanel.add(new JLabel("ȸ�����̵�: " + memberId)); // JPanel�� ������Ʈ�� ����
		selectPanel.add(new JLabel("/ ����: " + today));			
		selectPanel.add(new JLabel("/ ���� ����  "));
		selectPanel.add(Payment);
		selectPanel.add(new JLabel("  ���� ����  "));
		selectPanel.add(discount);
		btnPanel.add(btn_reserve);
		btnPanel.add(btn_cancel);
		btnPanel.add(new JLabel("���õ� �� ����"));		
		btnPanel.add(selectedSched);
		
		c.add(selectPanel); // �����ӿ� ����
		c.add(btnPanel);
		
		JScrollPane rsSP = new JScrollPane(schedulesTable);
		rsSP.setPreferredSize(new Dimension(850, 300));	
		
		select_Schedules(); // �� ���� ��ȸ
		
		c.add(rsSP);
		
		schedulesTable.addMouseListener(this); // JTable�� ��ư�� �̺�Ʈ������ ����
		btn_reserve.addActionListener(this);
		btn_cancel.addActionListener(this);
		
		setSize(900, 450);
		setVisible(true);
		
	}
	
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == btn_reserve) { // ���� ��ư�� ������ �� 
									
			try { // ���� ��¥�� �� ������ ��¥�� ���Ͽ� ������ �������� �˸�â ���
				String date1 = today;
				String date2 = screenDay;	
				
				if(date1.compareTo(date2) > 0) {
					msg.showMessageDialog(null, "�̹� �� ����� �����Դϴ�.");		
					return;
				}
				
			} catch (Exception e1) { // �� ������ ���õ��� ���� ��� ���� ó��
				msg.showMessageDialog(null, "�� ������ �������ּ���.");
				return;
			}			
			
			make_Reserve(); // ���� ��� ȣ��	
		
		}
		
		else if(e.getSource() == btn_cancel) { // ��� ���� �� ���� â ����
			dispose();			
		}
		
	}
		
	@Override
	public void mouseClicked(MouseEvent e) { // ���콺�� Table Ŭ�� �� ���� �ֽ�ȭ
		int row = schedulesTable.getSelectedRow();
		scheduleId = (int) schedulesTable.getModel().getValueAt(row, 0);
		hallId = (int) schedulesTable.getModel().getValueAt(row, 1);	
		screenDay = schedulesTable.getModel().getValueAt(row, 2).toString();
		selectedSched.setText("" + scheduleId + "��");
	}	
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}	
	@Override
	public void mouseExited(MouseEvent e) {}
	
	private void select_Schedules() { // �� ���� ��ȸ ���
		
		String query;	
		
		try {
			Statement stmt = con.createStatement();
			query = "Select scheduleId, hallId, openingDate, dayofweek, StartTime from schedules where MovieId = " + movieId + ";";
			
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next()) {
				
				Vector factor = new Vector();
				factor.add(rs.getInt(1));
				factor.add(rs.getInt(2));
				factor.add(rs.getString(3));
				factor.add(rs.getString(4));
				factor.add(rs.getString(5));
				
				model.addRow(factor);
				
			}
			
		}catch(SQLException exceptions) {
			msg.showMessageDialog(null, "���� ���� ����");
		}		
		
	}
	
	private void make_Reserve() { // ���� ���
		
		String query = "";
		ResultSet count;
		int reserveId = 0;
		int ticketId = 0;
		int seatId = 0;
		
		if(hallId == 0) {
			msg.showMessageDialog(null, "�� ������ �������ּ���.");
			return;
		}		
		
		try {
			
			Statement stmt = con.createStatement();
			
			count = stmt.executeQuery("select max(reservationid) from reservations;");
			count.next();
			reserveId = count.getInt(1) + 1;
			
			if(discount.getSelectedItem().toString().equals("����")) {				
				query = "insert into reservations values(" + reserveId + ", '" + Payment.getSelectedItem().toString() + "', '�������', 12000, "+ memberId +", str_to_date('" + today +  "', '%Y-%m-%d'));";
			}
			
			else {
				query = "insert into reservations values(" + reserveId + ", '" + Payment.getSelectedItem().toString() + "', '�������', 9000, "+ memberId +", str_to_date('" + today +  "', '%Y-%m-%d'));";			
				sellingPrice = 9000;
			}
			
			stmt.executeUpdate(query);
			
			count = stmt.executeQuery("select count(seatid) from seats;");
			count.next();
			seatId = count.getInt(1) + 1;
			
			query = "insert into seats values(" + seatId + ", " + hallId + ", true);";
			
			stmt.executeUpdate(query);
			
			count = stmt.executeQuery("select max(ticketid) from tickets;");
			count.next();
			ticketId = count.getInt(1) + 1;
			
			query = "insert into tickets values(*, *, *, *, *, false, 12000, *);";
			
			query = query.replaceFirst("\\*", "" + ticketId);
			query = query.replaceFirst("\\*", "" + scheduleId);
			query = query.replaceFirst("\\*", "" + hallId);
			query = query.replaceFirst("\\*", "" + seatId);
			query = query.replaceFirst("\\*", "" + reserveId);
			query = query.replaceFirst("\\*", "" + sellingPrice);
			
			stmt.executeUpdate(query);			
			msg.showMessageDialog(null, "���Ű� �Ϸ�Ǿ����ϴ�.");
			
		}catch(SQLException exception) {
			msg.showMessageDialog(null, "�� ������ �������ּ���.");
		}
	}
	
}

class myReservation extends JFrame implements ActionListener, MouseListener{ // ���� ���� ���� ��
	
	Connection con;
	ResultSet rs;
	
	JOptionPane msg = new JOptionPane(); 
	JButton delete = new JButton("����");
	JButton change_movie = new JButton("��ȭ ����");
	JButton change_sche = new JButton("���� ����");
	JButton close = new JButton("�ݱ�");
	JTextField selectedReserve = new JTextField(10); // ������ �� ������ ����� ��
		
	String title;
	String today;
	int memberId;	
	int ticketId = 0;
	int seatId;
	int reservationId;
	int movieId;
	
	String[] myReserve_property = {"Ƽ�Ϲ�ȣ", "��ȭ��", "����", "�󿵰���ȣ", "�¼���ȣ", "�ǸŰ���"}; 
	DefaultTableModel model = new DefaultTableModel(myReserve_property, 0) { // ������ ��ȭ�� �� ������ ������ model		
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};
	
	JTable myReserveTable = new JTable(model);	
	
	public myReservation(Connection con, int memberId, String today) { // ���� ���� ���� �� ������
		
		this.con = con;
		this.memberId = memberId;
		this.today = today;
		
		setTitle("���� ���� ����");
		
		Container c = getContentPane();
		c.setLayout(new FlowLayout());
		
		JScrollPane rsSP = new JScrollPane(myReserveTable);
		rsSP.setPreferredSize(new Dimension(850, 300));	
		
		selectedReserve.setEditable(false);
		
		select_myReservation(); // �� ���� ��ȸ
		
		JPanel upper = new JPanel();
		
		upper.add(new JLabel("ȸ��ID: " + memberId));
		upper.add(new JLabel(" / ���õ� Ƽ�� ��ȣ: "));
		upper.add(selectedReserve);
		upper.add(delete);
		upper.add(change_movie);
		upper.add(change_sche);
		upper.add(close);
		
		c.add(upper);	
		c.add(rsSP);
		
		myReserveTable.addMouseListener(this);
		delete.addActionListener(this);
		change_movie.addActionListener(this);
		change_sche.addActionListener(this);
		close.addActionListener(this);
		
		setSize(900, 400);
		setVisible(true);
		
	}
	
	@Override
	public void mouseClicked(MouseEvent e) { // ���콺�� Table Ŭ�� �� ���� �ֽ�ȭ
		int row = myReserveTable.getSelectedRow();
		ticketId = (int)myReserveTable.getModel().getValueAt(row, 0);	
		title = myReserveTable.getModel().getValueAt(row, 1).toString();	
		ShowInformation show = new ShowInformation(con, ticketId);	 // Ŭ���� ���� ������ ���� �� ���� ��� �� ȣ��	
		
		try {
			
			Statement stmt = con.createStatement();
			String query = "Select * from tickets where ticketId =" + ticketId + ";";
			
			rs = stmt.executeQuery(query);

			rs.next();
		
			seatId = rs.getInt(4);
			reservationId = rs.getInt(5);
			selectedReserve.setText("" + ticketId);
			
			query = "Select movieId from movies where title LIKE '%" + title + "%'";
			rs = stmt.executeQuery(query);
			
			rs.next();
			
			movieId = rs.getInt(1);
			
			
		}catch(Exception exception) {}
				
	}	
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}	
	@Override
	public void mouseExited(MouseEvent e) {}
	
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == close) // �ݱ� ��ư Ŭ�� �� ������ �ݱ�
			this.dispose();
		
		else if(e.getSource() == delete) { // ���� ���� ���� ��ư Ŭ�� �� ���� �� ���̺� �ֽ�ȭ
			delete_Reservation();
			select_myReservation();
		}

		else if(e.getSource() == change_movie) { // ��ȭ ���� ��ư Ŭ�� �� ��ȭ ���� �� ȣ��
			if(ticketId == 0) { // ���õ� ���� ������ ���� ��� �޼��� ǥ��
				msg.showMessageDialog(null, "������ ���� ������ �������ּ���.");
				return;
			}
			ChangeMovie chmv = new ChangeMovie(con, movieId, ticketId, seatId, memberId, today); // ��ȭ ���� �� ȣ��
			select_myReservation(); // ���̺� �ֽ�ȭ
		}
		
		else if(e.getSource() == change_sche) {	// ���� ���� ��ư Ŭ�� �� ���� ���� �� ȣ��	
			if(movieId == 0) { // ���õ� ���� ������ ���� ��� �޼��� ǥ��
				msg.showMessageDialog(null, "������ ���� ������ �������ּ���.");
				return;
			}			
			ChangeSchedule cs = new ChangeSchedule(con, movieId, ticketId, seatId, memberId, today);// �� ���� ���� �� ȣ��
			select_myReservation(); // ���̺� �ֽ�ȭ
		}
		
	}
	
	private void select_myReservation() { // ���� ���� ������ DB�κ��� �ҷ����� �޼ҵ�
		
		String query;
		model.setNumRows(0); // ���̺� �ʱ�ȭ
		
		try {
			
			Statement stmt = con.createStatement();
			query = "select s2.ticketId, movies.title, schedules.openingDate, s2.Hallid, s2.seatid, s2.sellingprice\r\n" // ������ ������ ��ȭ�� ���� ������ Ž���ϴ� ������
					+ "from movies, schedules, \r\n"
					+ "(select ticketId, scheduleid, hallid, seatid, sellingprice\r\n"
					+ "from tickets, (select reservationid from reservations where memberId = "
					+ memberId + ") s1 \r\n"
					+ "where tickets.reservationid = s1.Reservationid) s2\r\n"
					+ "where s2.scheduleid = schedules.ScheduleId and movies.Movieid = schedules.MovieId;";
			
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next()) { // ���� ����� ���̺� �Է�
				
				Vector factor = new Vector();
				factor.add(rs.getInt(1));
				factor.add(rs.getString(2));
				factor.add(rs.getString(3));
				factor.add(rs.getString(4));
				factor.add(rs.getString(5));
				factor.add(rs.getString(6));
				
				model.addRow(factor);
				
			}			
			
		}catch(SQLException exceptions) {
			msg.showMessageDialog(null, "���� ���� ����");
		}		
	}
	
	private void delete_Reservation() { // ���� ���� ���� �޼ҵ�
		
		String query;
		
		try {			
			
			if(ticketId == 0) throw new Exception(); // ���õ� ���� ������ ���� ��� ���� ó��
			
			Statement stmt = con.createStatement();
			
			query = "delete from tickets where ticketId = " + ticketId + ";"; // Ƽ�� ���� ������ ����
			stmt.execute(query);
			
			query = "update seats set seatStatus = false where seatId = " + seatId + ";"; // �¼� ���� �̻������ ����
			stmt.execute(query);
			
			query = "delete from reservations where reservationId = " + reservationId + ";"; // ���� ���� ����
			stmt.execute(query);	
			
			msg.showMessageDialog(null, "������ �Ϸ�Ǿ����ϴ�.");
			
		}catch(Exception exception) {
			msg.showMessageDialog(null, "������ ���� ������ �������ּ���.");			
		}
		
	}
		
}

class ShowInformation extends JFrame implements ActionListener{ // ���� ������ Ŭ���ϸ� �� ������ ǥ���� �ִ� ��
	
	Connection con;
	ResultSet rs;
	String query;
	
	int ticketId;
	int scheduleId;
	int hallId;

	JTextArea printArea = new JTextArea(19, 78);
	JButton close = new JButton("�ݱ�");
	
	public ShowInformation(Connection con, int ticketId) { // �� ���� ǥ�� �� ������
		
		this.con = con;
		this.ticketId = ticketId;
		
		setTitle("���� ���� �� ��ȸ");
		
		Container c = getContentPane();
		c.setLayout(new FlowLayout());
		printArea.setEditable(false);
		
		try {

			Statement stmt = con.createStatement();
			
			query = "Select * from tickets where ticketId =" + ticketId + ";";
			ResultSet rs = stmt.executeQuery(query);

			rs.next();
			
			scheduleId = rs.getInt(2);
			hallId = rs.getInt(3);
			
			query = "Select * from Schedules where scheduleId = " +scheduleId+ ";"; // ������ ���� ��ȸ
			rs=stmt.executeQuery(query);
			printArea.append("������\nScheduleId\tMovieId\tHallId\tOpeningDay\tDayofweek\tTimes\tStartTime\n");
			while(rs.next()) {
	  	  		printArea.append(""+rs.getInt(1));
	  	  		printArea.append("\t"+rs.getInt(2));
	  	  		printArea.append("\t"+rs.getInt(3));
	  	  		printArea.append("\t"+rs.getString(4));
	  	  		printArea.append("\t"+rs.getString(5));
	  	  		printArea.append("\t"+rs.getInt(6));
	  	  		printArea.append("\t"+rs.getString(7)+"\n");	  	  		
			}	  	  	 
			printArea.append("\n");
						
	  	  	query = "Select * from Halls where hallId = " + hallId + ";"; // �󿵰� ���� ��ȸ
			rs=stmt.executeQuery(query);
			printArea.append("�󿵰�\nHallId\tnumSeat\tHallStatus\n");
			while(rs.next()) {
	  	  		printArea.append(""+rs.getInt(1));
	  	  		printArea.append("\t"+rs.getInt(2));
	  	  		printArea.append("\t"+rs.getBoolean(3)+"\n");	  	  		
			}	  	  	 
			printArea.append("\n");
			
			query = "Select * from Tickets where ticketId = " + ticketId + ";"; // Ƽ�� ���� ��ȸ
			rs=stmt.executeQuery(query);
			printArea.append("Ƽ��\nTicketId\tScheduleId\tHallId\tSeatId\tReservationId\tIssueStatus\tPrice\tSellingPrice\n");
			while(rs.next()) {
	  	  		printArea.append(""+rs.getInt(1));
	  	  		printArea.append("\t"+rs.getInt(2));
	  	  		printArea.append("\t"+rs.getInt(3));
	  	  		printArea.append("\t"+rs.getInt(4));
	  	  		printArea.append("\t"+rs.getInt(5));
	  	  		printArea.append("\t"+rs.getBoolean(6));
	  	  		printArea.append("\t"+rs.getInt(7));
	  	  		printArea.append("\t"+rs.getInt(8)+"\n");	  	  		
			}	  	  	 
			printArea.append("\n");			
			
			
		}catch (SQLException e) {
			printArea.setText("SQL ���� ����");
		} 
		
		c.add(close);
		c.add(printArea);
		c.add(new JScrollPane(printArea));
		
		close.addActionListener(this);
		
		setSize(900, 450);
		setVisible(true);
		
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == close)
			this.dispose();
	}
	
}

class ChangeMovie extends JFrame implements ActionListener, MouseListener { // ��ȭ ���� ��
	
	Connection con;
	ResultSet rs;
	
	int selectedId = 0;
	int ticketId;
	int memberId;
	int seatId;
	int currentMovieId;
	String today;
	
	JOptionPane msg = new JOptionPane(); // �˾� �޼��� ��¿� JOptionPane
	
	JTextField inputTitle = new JTextField(12); // �˻��� �Է� ��
	JTextField inputDirector = new JTextField(12);
	JTextField inputActor = new JTextField(12);
	JTextField inputGenre = new JTextField(12);
	JTextField selectedMovie = new JTextField(10);
	JButton btn_movies = new JButton("��ȸ"); // �˻� ��ư
	JButton btn_reserve = new JButton("����"); // ���� ��ư
	JButton btn_close = new JButton("���"); // ��� ��ư 
	
	String[] movies_property = {"MovieId", "��ȭ��", "�󿵽ð�", "�󿵵��", "������", "����", "�帣", "��ȭ�Ұ�", "������"}; 
	DefaultTableModel model = new DefaultTableModel(movies_property, 0) {		// JTable�� ����� Model
		@Override
		public boolean isCellEditable(int row, int column) { // ���� ��� ����
			return false;
		}
	};
	
	JTable resultTable;
	
	public ChangeMovie(Connection con, int movieId, int ticketId, int seatId, int memberId, String today) { // ��ȭ ���� �� ������
		
		this.con = con;
		this.ticketId = ticketId;
		this.memberId = memberId;
		this.today = today;
		this.seatId = seatId;
		this.currentMovieId = movieId;
		
		setTitle("��ȭ ����");
		
		Container c = getContentPane();
		c.setLayout(new FlowLayout());
		
		JPanel inputForm = new JPanel();
		
		inputForm.add(new JLabel("��ȭ��")); 
		inputForm.add(inputTitle);
		inputForm.add(new JLabel("    ������"));
		inputForm.add(inputDirector);
		inputForm.add(new JLabel("    ����"));
		inputForm.add(inputActor);
		inputForm.add(new JLabel("    �帣"));
		inputForm.add(inputGenre);
		inputForm.add(btn_movies);		
		
		JPanel reservePan = new JPanel();
		selectedMovie.setEditable(false); // ���õ� ��ȭ ���� ��� �� ���� ��� ����		
		
		reservePan.add(btn_reserve);
		reservePan.add(btn_close);
		reservePan.add(new JLabel(" ȸ�� ID: "+memberId));
		reservePan.add(new JLabel(" /  ���� ��¥: "+today));
		reservePan.add(new JLabel(" / ���õ� ��ȭ "));
		reservePan.add(selectedMovie);

		resultTable = new JTable(model);
		JScrollPane rsSP = new JScrollPane(resultTable);
		rsSP.setPreferredSize(new Dimension(850, 300));		
		
		c.add(inputForm);
		c.add(reservePan);
		c.add(rsSP);
				
		btn_movies.addActionListener(this); // ��ȸ ��ư
		btn_reserve.addActionListener(this); // ���� ��ư
		btn_close.addActionListener(this); // ��� ��ư
		
		resultTable.addMouseListener(this); // ��ȸ ��� Ŭ�� �� �̺�Ʈ ����
				
		setSize(900, 450);
		setVisible(true);
		
	}
	
	public void actionPerformed(ActionEvent e) { // ��ư Ŭ���� ���� ����
		
		if(e.getSource() == btn_movies) { // ��ȭ �˻� ��ȸ
			select_movie();
		}
		
		else if(e.getSource() == btn_reserve) { // ���� ��ư
			
			if(selectedId == 0) { // ��ȭ�� �������� �ʾ��� �� �˸�â ���
				msg.showMessageDialog(null, "��ȭ�� �������ּ���.");
				return;
			}			
			
			if(selectedId == currentMovieId) { // �����Ϸ��� ��ȭ�� ���� ���� ������ ���� ��ȭ�� ��� �޼��� ǥ��
				msg.showMessageDialog(null, "���� ���ų����� ���� ��ȭ�Դϴ�.\n���� ������ ���Ͻø� ���� ���������� ���� ������ �������ּ���.");
				return;
			}		

			ChangeSchedule cs = new ChangeSchedule(con, selectedId, ticketId, seatId, memberId, today);// ������ ���� �� ȣ��

		}
		
		else if(e.getSource() == btn_close)
			this.dispose();
		
	}
	
	@Override
	public void mouseClicked(MouseEvent e) { // JTable Ŭ�� �� ���� �ֽ�ȭ
		int row = resultTable.getSelectedRow();
		selectedId = (int) resultTable.getModel().getValueAt(row, 0);	
		selectedMovie.setText(resultTable.getModel().getValueAt(row, 1).toString());
	}	
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}	
	@Override
	public void mouseExited(MouseEvent e) {}
	
	private void select_movie() { // �˻� ��ȸ ���

		JOptionPane msg = new JOptionPane();
		String query = "";
		boolean notFirst = false;
		model.setNumRows(0);	
		
		try {
			
			query = "Select * from Movies where "; // ������ �ʱ�ȭ
			
			if(!inputTitle.getText().equals("")) {
				query += "title LIKE '%" + inputTitle.getText() + "%'";
				notFirst = true;
			}
			if(!inputDirector.getText().equals("")) {
				if(notFirst)
					query += " and ";
				query += "director LIKE '%" + inputDirector.getText() + "%'";
				notFirst = true;
			}
			if(!inputActor.getText().equals("")) {
				if(notFirst)
					query += " and ";
				query += "actor LIKE '%" + inputActor.getText() + "%'";
				notFirst = true;
			}
			if(!inputGenre.getText().equals("")) {
				if(notFirst)
					query += " and ";
				query += "genre LIKE '%" + inputGenre.getText() + "%'";
				notFirst = true;
			}			
			
			query += ";";
			
			Statement stmt = con.createStatement();
			rs = stmt.executeQuery(query); // ������ ���� �� ����� ��ȯ�޾� ����
			
			while(rs.next()) { // JTable�� ����
				
				Vector factor = new Vector();
				factor.add(rs.getInt(1));
				factor.add(rs.getString(2));
				factor.add(rs.getString(3));
				factor.add(rs.getInt(4));
				factor.add(rs.getString(5));
				factor.add(rs.getString(6));
				factor.add(rs.getString(7));
				factor.add(rs.getString(8));
				factor.add(rs.getString(9));	
				
				model.addRow(factor);
				
			}			
			
		}catch(SQLException exception) {
			msg.showMessageDialog(null, "�˻� Ű���带 �Է����ּ���.");
		}
		
	}	
	
}

class ChangeSchedule extends JFrame implements ActionListener, MouseListener { // ���� ���� ��
	
	Connection con;
	JOptionPane msg = new JOptionPane(); 
	
	String today;
	String screenDay;
	int memberId;
	int movieId;
	int scheduleId;
	int hallId;
	int ticketId;
	int seatId;
	int currentScheId;
	
	JButton btn_reserve = new JButton("����");
	JButton btn_cancel = new JButton("���");
	JTextField selectedSched = new JTextField(10); // ������ �� ������ ����� ��
	
	String[] schedules_property = {"ScheduleId", "�󿵰�", "��¥", "����","�ð�"}; 
	DefaultTableModel model = new DefaultTableModel(schedules_property, 0) { // ������ ��ȭ�� �� ������ ������ model		
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};
	
	JTable schedulesTable = new JTable(model);
	
	public ChangeSchedule (Connection con, int movieId, int ticketId, int seatId, int memberId, String today) { // ���� ���� �� ������
		
		this.con = con;
		this.memberId = memberId; 
		this.movieId = movieId;
		this.today = today;
		this.ticketId = ticketId;
		this.seatId = seatId;
		
		setTitle("������ ����");
		
		Container c = getContentPane();
		c.setLayout(new FlowLayout());
		
		selectedSched.setEditable(false); // ���õ� �� ���� ���â�� ���� ��� ����
		
		JPanel selectPanel = new JPanel(); // ������Ʈ���� �׷�ȭ�� JPanel ����
		JPanel btnPanel = new JPanel();
		
		selectPanel.add(new JLabel("ȸ��ID: " + memberId)); // JPanel�� ������Ʈ�� ����
		selectPanel.add(new JLabel("/ ����: " + today));		

		btnPanel.add(btn_reserve);
		btnPanel.add(btn_cancel);
		btnPanel.add(new JLabel("���õ� �� ����"));		
		btnPanel.add(selectedSched);
		
		c.add(selectPanel); // �����ӿ� ����
		c.add(btnPanel);
		
		JScrollPane rsSP = new JScrollPane(schedulesTable);
		rsSP.setPreferredSize(new Dimension(850, 300));	
		
		select_Schedules(); // �� ���� ��ȸ
		
		c.add(rsSP);
		
		schedulesTable.addMouseListener(this); // JTable�� ��ư�� �̺�Ʈ������ ����
		btn_reserve.addActionListener(this);
		btn_cancel.addActionListener(this);
		
		setSize(900, 450);
		setVisible(true);
		
	}
	
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == btn_reserve) { // ���� ��ư�� ������ �� 
									
			try { // ���� ��¥�� �� ������ ��¥�� ���Ͽ� ������ �������� �˸�â ���
				String date1 = today;
				String date2 = screenDay;	
				
				if(date1.compareTo(date2) > 0) {
					msg.showMessageDialog(null, "�̹� �� ����� �����Դϴ�.");		
					return;
				}
				
			} catch (Exception e1) { // �� ������ ���õ��� ���� ��� ���� ó��
				msg.showMessageDialog(null, "�� ������ �������ּ���.");
				return;
			}			
			
			UpdateReservation(); // ���� ��� ȣ��	
		
		}
		
		else if(e.getSource() == btn_cancel) { // ��� ���� �� ���� â ����
			this.dispose();			
		}
		
	}
		
	@Override
	public void mouseClicked(MouseEvent e) { // ���콺�� Table Ŭ�� �� ���� �ֽ�ȭ
		int row = schedulesTable.getSelectedRow();
		scheduleId = (int) schedulesTable.getModel().getValueAt(row, 0);
		hallId = (int) schedulesTable.getModel().getValueAt(row, 1);	
		screenDay = schedulesTable.getModel().getValueAt(row, 2).toString();
		selectedSched.setText("" + scheduleId + "��");
	}	
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}	
	@Override
	public void mouseExited(MouseEvent e) {}
	
	private void select_Schedules() { // �� ���� ��ȸ ���
		
		String query;	
		
		try {
			Statement stmt = con.createStatement();
			query = "Select scheduleId, hallId, openingDate, dayofweek, StartTime from schedules where MovieId = " + movieId + ";";
			
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next()) {
				
				Vector factor = new Vector();
				factor.add(rs.getInt(1));
				factor.add(rs.getInt(2));
				factor.add(rs.getString(3));
				factor.add(rs.getString(4));
				factor.add(rs.getString(5));
				
				model.addRow(factor);
				
			}
			
		}catch(SQLException exceptions) {
			msg.showMessageDialog(null, "���� ���� ����");
		}		
		
	}
	
	private void UpdateReservation() { // ���� ���� �޼ҵ�
		
		String query = "";
		ResultSet count;
		int newSeatId = 0;
				
		try {
			
			Statement stmt = con.createStatement();
			
			query = "select scheduleId from tickets where ticketId =" + ticketId +";"; // ���� ������ �� ���� ��ȸ �� ���� 
			count = stmt.executeQuery(query);
			count.next();
			currentScheId = count.getInt(1);
			
			if(scheduleId == currentScheId) { // �����Ϸ��� �� ������ ���� �� ������ �ߺ��Ǹ� �޼��� ǥ��
				msg.showMessageDialog(null, "���� ���ų����� ���� �������Դϴ�.\n���� ������ ���Ͻø� �ٸ� ������ �������ּ���.");
				return;
			};
			
			query = "update seats set seatStatus = false where seatId = " + seatId + ";"; // ���� �� �¼� ��뿩�� �̻������ ����
			stmt.executeUpdate(query);			
			
			count = stmt.executeQuery("select count(seatid) from seats;");
			count.next();
			newSeatId = count.getInt(1) + 1;
			
			query = "insert into seats values(" + newSeatId + ", " + hallId + ", true);"; // ���ο� �¼� ���� ���
			stmt.executeUpdate(query);
			
			query = "UPDATE tickets SET scheduleId =" + scheduleId + " where ticketId = " + ticketId + ";"; // Ƽ���� �� ���� ���� ����
			stmt.executeUpdate(query);
			
			query = "UPDATE tickets SET hallId=" + hallId + " where ticketId = " + ticketId + ";"; // Ƽ���� �󿵰� ���� ����
			stmt.executeUpdate(query);
			
			query = "UPDATE tickets SET seatId =" + newSeatId + " where ticketId = " + ticketId + ";"; // Ƽ���� �¼� ���� ����
			stmt.executeUpdate(query);
						
			msg.showMessageDialog(null, "������ �Ϸ�Ǿ����ϴ�.");
			
		}catch(SQLException exception) {
			msg.showMessageDialog(null, "�� ������ �������ּ���.");
		}
		
	}
	
}

class adminF extends JFrame implements ActionListener {

	Connection con;
	JButton btn_init;
	JButton btn_insert;
	JButton btn_delete;
	JButton btn_update;
	JButton btn_select;
	JTextArea printArea = new JTextArea(19, 78);

	public adminF(Connection con) {

		setTitle("������");

		this.con = con;

		Container c = getContentPane();
		c.setLayout(new FlowLayout());
		printArea.setEditable(false);

		btn_init = new JButton("�ʱ�ȭ");
		btn_insert = new JButton("�Է�");
		btn_update = new JButton("����");
		btn_delete = new JButton("����");
		btn_select = new JButton("�˻�");

		JPanel btns = new JPanel();

		btns.add(btn_init);
		btns.add(btn_insert);
		btns.add(btn_delete);
		btns.add(btn_update);
		btns.add(btn_select);

		c.add(btns);
		c.add(printArea);
		c.add(new JScrollPane(printArea));

		btn_init.addActionListener(this);
		btn_insert.addActionListener(this);
		btn_update.addActionListener(this);
		btn_delete.addActionListener(this);
		btn_select.addActionListener(this);

		setSize(900, 450);
		setVisible(true);

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == btn_init) {
			Initial init = new Initial(con);
			printArea.setText("");			
		} else if (e.getSource() == btn_insert) {
			Insert insert = new Insert(con);
		} else if (e.getSource() == btn_update) {
			Update update = new Update(con);
		} else if (e.getSource() == btn_delete) {
			Delete delete = new Delete(con);
		} else if (e.getSource() == btn_select) {
			select_table();
		}

	}  
	
	private void select_table() {
		
		printArea.setText("");
		String query;
		
		try {

			Statement stmt = con.createStatement();
			
			query = "Select * from Movies;";
			ResultSet rs=stmt.executeQuery(query);
			printArea.append("��ȭ\nMovieId\tTitle\tRunningTime\tRating\tDirector\t\tActor\t\tGenre\tSynopsis\t\tReleaseDay\n");
			while(rs.next()) {
	  	  		printArea.append(""+rs.getInt(1));
	  	  		printArea.append("\t"+rs.getString(2));
	  	  		printArea.append("\t"+rs.getString(3));
	 		  	printArea.append("\t"+rs.getInt(4));
	 		  	printArea.append("\t"+rs.getString(5));
	  	  		printArea.append("\t\t"+rs.getString(6));
	 		  	printArea.append("\t\t"+rs.getString(7));
	  	  		printArea.append("\t"+rs.getString(8));
	  	  		printArea.append("\t\t"+rs.getString(9)+"\n");	  	  		
			}	  	  	 
			printArea.append("\n");
			
	  	  	query = "Select * from Halls;";
			rs=stmt.executeQuery(query);
			printArea.append("�󿵰�\nHallId\tnumSeat\tHallStatus\n");
			while(rs.next()) {
	  	  		printArea.append(""+rs.getInt(1));
	  	  		printArea.append("\t"+rs.getInt(2));
	  	  		printArea.append("\t"+rs.getBoolean(3)+"\n");	  	  		
			}	  	  	 
			printArea.append("\n");
			
			query = "Select * from Schedules;";
			rs=stmt.executeQuery(query);
			printArea.append("������\nScheduleId\tMovieId\tHallId\tOpeningDay\tDayofweek\tTimes\tStartTime\n");
			while(rs.next()) {
	  	  		printArea.append(""+rs.getInt(1));
	  	  		printArea.append("\t"+rs.getInt(2));
	  	  		printArea.append("\t"+rs.getInt(3));
	  	  		printArea.append("\t"+rs.getString(4));
	  	  		printArea.append("\t"+rs.getString(5));
	  	  		printArea.append("\t"+rs.getInt(6));
	  	  		printArea.append("\t"+rs.getString(7)+"\n");	  	  		
			}	  	  	 
			printArea.append("\n");
			
			query = "Select * from Members;";
			rs=stmt.executeQuery(query);
			printArea.append("ȸ��\nMemberId\tMemberName\tPhone\t\tEmail\n");
			while(rs.next()) {
	  	  		printArea.append(""+rs.getInt(1));
	  	  		printArea.append("\t"+rs.getString(2));
	  	  		printArea.append("\t"+rs.getString(3));
	  	  		printArea.append("\t\t"+rs.getString(4)+"\n");	  	  		
			}	  	  	 
			printArea.append("\n");
	  	  	
			query = "Select * from Reservations;";
			rs=stmt.executeQuery(query);
			printArea.append("��������\nReservationId\tPayment\tPayStatus\tPrice\tMemberId\tPayDate\n");
			while(rs.next()) {
	  	  		printArea.append(""+rs.getInt(1));
	  	  		printArea.append("\t"+rs.getString(2));
	  	  		printArea.append("\t"+rs.getString(3));
	  	  		printArea.append("\t"+rs.getInt(4));
	  	  		printArea.append("\t"+rs.getInt(5));
	  	  		printArea.append("\t"+rs.getString(6)+"\n");	  	  		
			}	  	  	 
			printArea.append("\n");
			
			query = "Select * from Seats;";
			rs=stmt.executeQuery(query);
			printArea.append("�¼�\nSeatId\tHallId\tSeatStatus\n");
			while(rs.next()) {
	  	  		printArea.append(""+rs.getInt(1));
	  	  		printArea.append("\t"+rs.getInt(2));
	  	  		printArea.append("\t"+rs.getBoolean(3)+"\n");	  	  		
			}	  	  	 
			printArea.append("\n");
			
			query = "Select * from Tickets;";
			rs=stmt.executeQuery(query);
			printArea.append("Ƽ��\nTicketId\tScheduleId\tHallId\tSeatId\tReservationId\tIssueStatus\tPrice\tSellingPrice\n");
			while(rs.next()) {
	  	  		printArea.append(""+rs.getInt(1));
	  	  		printArea.append("\t"+rs.getInt(2));
	  	  		printArea.append("\t"+rs.getInt(3));
	  	  		printArea.append("\t"+rs.getInt(4));
	  	  		printArea.append("\t"+rs.getInt(5));
	  	  		printArea.append("\t"+rs.getBoolean(6));
	  	  		printArea.append("\t"+rs.getInt(7));
	  	  		printArea.append("\t"+rs.getInt(8)+"\n");	  	  		
			}	  	  	 
			printArea.append("\n");			
		}catch (SQLException e) {
			printArea.setText("SQL ���� ����");
		} 
		
	}
	
}

class Initial extends JFrame implements ActionListener {

	Connection con;
	JButton btn_yes;
	JButton btn_no;
	JOptionPane msg;

	public Initial(Connection con) {

		setTitle("�ʱ�ȭ");

		this.con = con;
		msg = new JOptionPane();

		Container c = getContentPane();
		c.setLayout(new BorderLayout());

		btn_yes = new JButton("Yes");
		btn_no = new JButton("No");

		JLabel notice = new JLabel("DB�� �ʱ�ȭ �Ͻðڽ��ϱ�?");
		notice.setHorizontalAlignment(JLabel.CENTER);

		JPanel btn_set = new JPanel();
		btn_set.add(btn_yes);
		btn_set.add(btn_no);

		c.add(notice, BorderLayout.NORTH);
		c.add(btn_set, BorderLayout.SOUTH);

		btn_yes.addActionListener(this);
		btn_no.addActionListener(this);

		setSize(300, 110);
		setVisible(true);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_yes) {
			sql_init();
			this.dispose();
		} else if (e.getSource() == btn_no) {
			this.dispose();
		}
	}

	private void sql_init() {
		try {
			Statement stmt = con.createStatement();

			stmt.execute("drop table if exists tickets");
			stmt.execute("drop table if exists seats");
			stmt.execute("drop table if exists reservations");
			stmt.execute("drop table if exists members");
			stmt.execute("drop table if exists schedules");
			stmt.execute("drop table if exists halls");
			stmt.execute("drop table if exists movies");

			stmt.execute("CREATE TABLE Movies (\r\n" + "  Movieid INT primary key,  \r\n" + "  title VARCHAR(45),\r\n"
					+ "  RunningTime TIME,\r\n" + "  Rating INT,\r\n" + "  Director VARCHAR(45),\r\n"
					+ "  Actor VARCHAR(45),\r\n" + "  Genre VARCHAR(45),\r\n" + "  Synopsis VARCHAR(45),\r\n"
					+ "  ReleaseDay DATE\r\n" + "  );");

			stmt.execute("CREATE TABLE Halls (\r\n" + "  Hallid INT PRIMARY KEY,\r\n" + "  numSeat INT,\r\n"
					+ "  HallStatus TINYINT\r\n" + "  );");

			stmt.execute("CREATE TABLE Schedules (\r\n" + "  ScheduleId INT primary key,\r\n" + "  MovieId INT,\r\n"
					+ "  Hallid INT,\r\n" + "  openingDate DATE,\r\n" + "  dayofweek VARCHAR(45),\r\n"
					+ "  times INT,\r\n" + "  StartTime TIME,\r\n"
					+ "  Foreign key (MovieId) references movies(Movieid),\r\n"
					+ "  Foreign key (Hallid) references halls(hallid)\r\n" + "  );");

			stmt.execute("CREATE TABLE Seats (\r\n" + "  SeatId INT Primary key,\r\n" + "  Hallid INT,\r\n"
					+ "  SeatStatus TINYINT ,\r\n" + "  Foreign key (Hallid) references halls(hallid) \r\n" + "  );");

			stmt.execute(
					"CREATE TABLE Members (\r\n" + "  MemberId INT primary key,\r\n" + "  memberName VARCHAR(45),\r\n"
							+ "  Phone VARCHAR(45),\r\n" + "  Email VARCHAR(45)\r\n" + "  );");

			stmt.execute("CREATE TABLE Reservations (\r\n" + "  ReservationId INT Primary key,\r\n"
					+ "  Payment VARCHAR(45),\r\n" + "  PayStatus VARCHAR(45),\r\n" + "  Price INT,\r\n"
					+ "  memberId INT,\r\n" + "  PayDate DATE,\r\n"
					+ "  Foreign key (memberId) references members(memberid)\r\n" + "  );");

			stmt.execute("CREATE TABLE Tickets (\r\n" + "  TicketId INT primary key,\r\n" + "  ScheduleId INT,\r\n"
					+ "  Hallid INT,\r\n" + "  Seatid INT,\r\n" + "  Reservationid INT,\r\n"
					+ "  IssueStatus TINYINT,\r\n" + "  Price INT,\r\n" + "  SellingPrice INT,\r\n"
					+ "  foreign key (Scheduleid) references schedules(scheduleid),\r\n"
					+ "  foreign key (Hallid) references Halls(hallid),\r\n"
					+ "  foreign key (Seatid) references seats(seatid),\r\n"
					+ "  foreign key (Reservationid) references reservations(reservationId)\r\n" + "  );");

			stmt.execute(
					"insert into movies values(1, '���˵���2', 020000, 15, '�̻��', '������', '����, �׼�', '��õ�� ���¹��� ��Ʈ������ ������ �����ڸ� �ε��޾� ����� �̼��� �޴´�.', str_to_date('2021-01-14', '%Y-%m-%d'));");
			stmt.execute(
					"insert into movies values(2, '�ó׸�õ��', 020400, 0, '�꼼���丣���䷹', '�ʸ����ͷ�', '���', '�簳��, ��ȭ�� ������ �̾߱�', str_to_date('2021-02-22', '%Y-%m-%d'));\r\n");
			stmt.execute(
					"insert into movies values(3, '���̾��', 020500, 12, '���ĺ��', '�ι�Ʈ�ٿ���ִϾ�', 'SF, �׼�', '������ũ ���� ������� ź��!', str_to_date('2021-03-30', '%Y-%m-%d'));");
			stmt.execute(
					"insert into movies values(4, '���ũŻ��', 022200, 15, '����ũ�ٶ�Ʈ', '���κ�', '���', '�η����� �ʸ� �˼��� ���ΰ� ����� �ʸ� �����Ӱ� �ϸ���', str_to_date('2021-04-24', '%Y-%m-%d'));");
			stmt.execute(
					"insert into movies values(5, 'Ŭ����', 021200, 12, '�����', '�տ���', '���', '�쿬�� �׷��� �ݵ��, ������ ����� �����.', str_to_date('2021-05-30', '%Y-%m-%d'));");
			stmt.execute(
					"insert into movies values(6, 'Ÿ��Ÿ��', 031400, 15, '���ӽ�ī�޷�', '������������ī������', '���', '�������� ���� ������ ���� ���丮', str_to_date('2021-06-01', '%Y-%m-%d'));");
			stmt.execute(
					"insert into movies values(7, '����', 021200, 19, '�󺣼�', '�帣��', '����, �׼�', '���� �ູ�� ���� �;�. �ᵵ �ڰ�, �Ѹ��� �����ž�.', str_to_date('2021-07-11', '%Y-%m-%d'));");
			stmt.execute(
					"insert into movies values(8, '�������߾�', 021200, 15, '����ȣ', '�۰�ȣ', '����', '��ġ���� ��� �;����ϴ�. ����� �����ʴϱ�', str_to_date('2021-08-25', '%Y-%m-%d'));");
			stmt.execute(
					"insert into movies values(9, '�������', 022700, 12, '�ݸ�Ʈ�����ο�', 'ũ��������', '�׼�, ����', '���� ��� ���� ������.', str_to_date('2021-09-01', '%Y-%m-%d'));");
			stmt.execute(
					"insert into movies values(10, '���', 022400, 15, '�̼���', '�Ѽ���', '���', '�Ƶ��� ���� ��ġ �λ� �־��� ���⸦ �°� �� ����', str_to_date('2021-11-20', '%Y-%m-%d'));");

			stmt.execute("insert into halls values(1, 30, false);");
			stmt.execute("insert into halls values(2, 30, false);");
			stmt.execute("insert into halls values(3, 30, true);");
			stmt.execute("insert into halls values(4, 30, true);");
			stmt.execute("insert into halls values(5, 30, true);");
			stmt.execute("insert into halls values(6, 30, true);");
			stmt.execute("insert into halls values(7, 30, true);");
			stmt.execute("insert into halls values(8, 30, true);");
			stmt.execute("insert into halls values(9, 30, true);");
			stmt.execute("insert into halls values(10, 30, true);");

			stmt.execute(
					"insert into schedules values(1, 1, 1, str_to_date('2021-01-14', '%Y-%m-%d'), '��', 8, 093000);");
			stmt.execute(
					"insert into schedules values(2, 2, 3, str_to_date('2021-03-01','%Y-%m-%d'), '��', 6, 082000);");
			stmt.execute(
					"insert into schedules values(3, 3, 2, str_to_date('2021-04-01', '%Y-%m-%d'), '��', 10, 075000);");
			stmt.execute(
					"insert into schedules values(4, 3, 3, str_to_date('2021-04-01', '%Y-%m-%d'), '��', 9, 081000);");
			stmt.execute(
					"insert into schedules values(5, 4, 7, str_to_date('2021-04-28', '%Y-%m-%d'), '��', 7, 100000);");
			stmt.execute(
					"insert into schedules values(6, 5, 4, str_to_date('2021-06-01', '%Y-%m-%d'), 'ȭ', 8, 091000);");
			stmt.execute(
					"insert into schedules values(7, 6, 5, str_to_date('2021-06-01', '%Y-%m-%d'), 'ȭ', 7, 085000);");
			stmt.execute(
					"insert into schedules values(8, 7, 6, str_to_date('2021-07-11', '%Y-%m-%d'), '��', 6, 101000);");
			stmt.execute(
					"insert into schedules values(9, 8, 8, str_to_date('2021-08-26', '%Y-%m-%d'), '��', 7, 110000);");
			stmt.execute(
					"insert into schedules values(10, 9, 10, str_to_date('2021-09-12', '%Y-%m-%d'), '��', 8, 091500);");
			stmt.execute(
					"insert into schedules values(11, 10, 9, str_to_date('2021-11-20', '%Y-%m-%d'), '��', 6, 101000);");

			stmt.execute("insert into members values(1, '���缮', '010-1234-5678', 'qwerty@naver.com');");
			stmt.execute("insert into members values(2, '�ڸ��', '010-2143-6587', 'asdfgh@daum.net');");
			stmt.execute("insert into members values(3, '������', '010-9876-5432', 'zxcv@gmail.com');");
			stmt.execute("insert into members values(4, '������', '010-5678-9000', 'dodo@naver.com');");
			stmt.execute("insert into members values(5, '��ȫö', '010-7777-1234', 'hong123@gmail.com');");
			stmt.execute("insert into members values(6, '�ϵ���', '010-4232-5342', 'haha77@naver.com');");
			stmt.execute("insert into members values(7, '�漺��', '010-2233-4321', 'lee@daum.net');");
			stmt.execute("insert into members values(8, '����ȣ', '010-6541-6512', 'teo123@gmail.com');");
			stmt.execute("insert into members values(9, '������', '010-4569-4412', 'young33@gmail.com');");
			stmt.execute("insert into members values(10, '���ӽ�', '010-9123-4012', 'james00@gmail.com');");

			stmt.execute(
					"insert into reservations values(1, 'ī��', '�����Ϸ�', 9000, 3, str_to_date('2021-01-14', '%Y-%m-%d'));");
			stmt.execute(
					"insert into reservations values(2, '����', '�����Ϸ�', 12000, 6, str_to_date('2021-01-15', '%Y-%m-%d'));");
			stmt.execute(
					"insert into reservations values(3, 'ī��', '�����Ϸ�', 12000, 5, str_to_date('2021-03-02', '%Y-%m-%d'));");
			stmt.execute(
					"insert into reservations values(4, 'ī��', '�����Ϸ�', 9000, 1, str_to_date('2021-04-09', '%Y-%m-%d'));");
			stmt.execute(
					"insert into reservations values(5, 'ī��', '�����Ϸ�', 9000, 1, str_to_date('2021-06-03', '%Y-%m-%d'));");
			stmt.execute(
					"insert into reservations values(6, 'ī��', '�����Ϸ�', 12000, 1, str_to_date('2021-06-15', '%Y-%m-%d'));");
			stmt.execute(
					"insert into reservations values(7, '����', '�����Ϸ�', 8000, 4, str_to_date('2021-07-20', '%Y-%m-%d'));");
			stmt.execute(
					"insert into reservations values(8, 'ī��', '�����Ϸ�', 12000, 2, str_to_date('2021-08-27', '%Y-%m-%d'));");
			stmt.execute(
					"insert into reservations values(9, 'ī��', '�������', 9000, 1, str_to_date('2021-09-08', '%Y-%m-%d'));");
			stmt.execute(
					"insert into reservations values(10, '����', '�������', 12000, 9, str_to_date('2021-09-10', '%Y-%m-%d'));");
						
			stmt.execute("insert into seats values(1, 1, false);");
			stmt.execute("insert into seats values(2, 1, false);");
			stmt.execute("insert into seats values(3, 3, false);");
			stmt.execute("insert into seats values(4, 3, true);");
			stmt.execute("insert into seats values(5, 4, true);");
			stmt.execute("insert into seats values(6, 5, true);");
			stmt.execute("insert into seats values(7, 6, true);");
			stmt.execute("insert into seats values(8, 8, true);");
			stmt.execute("insert into seats values(9, 10, true);");
			stmt.execute("insert into seats values(10, 9, true);");
			

			stmt.execute("insert into tickets values(1, 1, 1, 1, 1, true, 12000, 9000);");
			stmt.execute("insert into tickets values(2, 2, 1, 2, 2, true, 12000, 12000);");
			stmt.execute("insert into tickets values(3, 2, 3, 3, 3, true, 12000, 12000);");
			stmt.execute("insert into tickets values(4, 4, 3, 4, 4, true, 12000, 9000);");
			stmt.execute("insert into tickets values(5, 5, 4, 5, 5, true, 12000, 9000);");
			stmt.execute("insert into tickets values(6, 7, 5, 6, 6, true, 12000, 12000);");
			stmt.execute("insert into tickets values(7, 8, 6, 7, 7, true, 12000, 8000);");
			stmt.execute("insert into tickets values(8, 9, 8, 8, 8, true, 12000, 12000);");
			stmt.execute("insert into tickets values(9, 10, 10, 9, 9, false, 12000, 9000);");
			stmt.execute("insert into tickets values(10, 11, 9, 10, 10, false, 12000, 12000);");

			msg.showMessageDialog(null, "�ʱ�ȭ �Ϸ�");
		} catch (SQLException exception) {
			msg.showMessageDialog(null, "���� ���� ����");
		}

	}

}

class Insert extends JFrame implements ActionListener {

	String table_name[] = { "��ȭ", "������", "�󿵰�", "Ƽ��", "�¼�", "ȸ����", "��������" };

	Connection con;
	JTextField writeArea = new JTextField(65);
	JComboBox<String> combo = new JComboBox<String>(table_name);
	JTextArea printArea = new JTextArea(15, 78);
	JButton btn1 = new JButton("����");
	JButton btn2 = new JButton("���");

	public Insert(Connection con) {

		setTitle("�Է�");

		this.con = con;
		printArea.setEditable(false);

		Container c = getContentPane();
		c.setLayout(new FlowLayout());
		printArea.setEditable(false);

		btn1.addActionListener(this);
		btn2.addActionListener(this);

		JPanel inPanel = new JPanel();

		inPanel.add(combo);
		inPanel.add(writeArea);

		c.add(btn1);
		c.add(btn2);
		c.add(inPanel);
		c.add(printArea);
		c.add(new JScrollPane(printArea));

		printArea.setText(" ���ο� �����͸� �Է��Ͻ÷��� �׸��� �����Ͻð� ���� �Է�â�� �����͸� �Է��� �� ���� ��ư�� �����ּ���.\n");
		printArea.append(" �� �Ӽ��� /�� �������ֽð� ��¥�� yyyy-mm-dd �������� �Է����ּ���. �󿵵���� ������ �Է����ֽð� ��ü�̿밡�� 0�� �Է����ּ���.\n");
		printArea.append(" �������, �߱ǿ����� ��� true �Ǵ� false�� �Է����ֽð� �ð��� hhmmss �������� �Է����ּ���.\n\n");
		printArea.append(" ��ȭ �Է� ���� : 1/���˵���2/020000/15/�̻��/������/����, �׼�/��ȭ �Ұ� �Է�/2021-01-14\n\n");
		printArea.append(" ��ȭ �Է� �� : ��ȭ��ȣ/��ȭ��/�󿵽ð�/�󿵵��/����/���/�帣/��ȭ�Ұ�/������\n");
		printArea.append(" ������ �Է� �� : ��������ȣ/��ȭ��ȣ/�󿵰���ȣ/�󿵽�����/�󿵿���/��ȸ��/�󿵽��۽ð�\n");
		printArea.append(" �󿵰� �Է� �� : �󿵰���ȣ/�¼���/�󿵰���뿩��\n");
		printArea.append(" Ƽ�� �Է� �� : Ƽ�Ϲ�ȣ/��������ȣ/�󿵰���ȣ/�¼���ȣ/���Ź�ȣ/�߱ǿ���/ǥ�ذ���/�ǸŰ���\n");
		printArea.append(" �¼� �Է� �� : �¼���ȣ/�󿵰���ȣ/�¼���뿩��\n");
		printArea.append(" ȸ���� �Է� �� : ȸ�����̵�/����/�޴�����ȣ/���ڸ����ּ�\n");
		printArea.append(" �������� �Է� �� : ���Ź�ȣ/�������/��������/�����ݾ�/ȸ�����̵�/��������\n");
		
		setSize(900, 450);
		setVisible(true);

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == btn1) {
			sql_insert();
		}

		else if (e.getSource() == btn2) {
			this.dispose();
		}

	}

	private void sql_insert() {
		
		String query = "";

		if (combo.getSelectedItem().toString().equals("��ȭ")) {
			query = "insert into movies values(*, '*', *, *, '*', '*', '*', '*', str_to_date('*', '%Y-%m-%d'));";
		} else if (combo.getSelectedItem().toString().equals("������")) {
			query = "insert into schedules values(*, *, *, str_to_date('*', '%Y-%m-%d'), '*', *, *);";
		} else if (combo.getSelectedItem().toString().equals("�󿵰�")) {
			query = "insert into halls values(*, *, *);";
		} else if (combo.getSelectedItem().toString().equals("Ƽ��")) {
			query = "insert into tickets values(*, *, *, *, *, *, *, *);";
		} else if (combo.getSelectedItem().toString().equals("�¼�")) {
			query = "insert into seats values(*, *, *);";
		} else if (combo.getSelectedItem().toString().equals("ȸ����")) {
			query = "insert into members values(*, '*', '*', '*');";
		} else if (combo.getSelectedItem().toString().equals("��������")) {
			query = "insert into reservations values(*, '*', '*', *, *, str_to_date('*', '%Y-%m-%d'));";
		}

		try {
			Statement stmt = con.createStatement();

			String input = writeArea.getText();
			String[] factor = input.split("/");

			for (int i = 0; i < factor.length; i++) {
				query = query.replaceFirst("\\*", factor[i]);
			}

			stmt.execute(query);

			printArea.setText("�Է� ����");
		} catch (SQLException e) {
			printArea.setText("�Է� ����, �ߺ��� Ű ���� ���ԵǾ� �ְų� ��Ŀ� �´� ���� �Է��Ͽ����� Ȯ�����ֽʽÿ�.\n�������� �ʴ� �ܷ�Ű�� ������ �Ұ����մϴ�.");
		} catch (NumberFormatException e) {
			printArea.setText("�Է� ����, �Է°��� /�� ������ �Է����ֽʽÿ�.\n");
			printArea.append("��¥�� yyyy-mm-dd �������� �Է����ֽʽÿ�.");
		} catch (IndexOutOfBoundsException e) {
			printArea.setText("�Է� ����, �Է°��� /�� ������ �Է����ֽʽÿ�.\n");
		}

	}

}

class Delete extends JFrame implements ActionListener {

	String table_name[] = { "��ȭ", "������", "�󿵰�", "Ƽ��", "�¼�", "ȸ����", "��������" };

	Connection con;

	JComboBox<String> combo = new JComboBox<String>(table_name);
	JTextField where = new JTextField(65);
	JTextArea printArea = new JTextArea(15, 78);
	JButton btn_delete = new JButton("����");
	JButton btn_cancel = new JButton("���");

	public Delete(Connection con) {

		this.con = con;

		setTitle("����");
		
		Container c = getContentPane();
		c.setLayout(new FlowLayout());
		printArea.setEditable(false);

		JPanel inPanel = new JPanel();

		inPanel.add(new JLabel("Where ����"));
		inPanel.add(where);

		c.add(combo);
		c.add(btn_delete);
		c.add(btn_cancel);
		c.add(inPanel);
		c.add(printArea);
		c.add(new JScrollPane(printArea));

		printArea.setText("���̺��� �����Ͻð� �Է� â�� ���ǽ��� �Է����ּ���.\n\n");
		printArea.append("where ���ǹ��� Ƽ���� ������ ��츦 ���� ��� ticketId = 1�� �������� �Է����ּ���.\n");
		printArea.append("�̿ܿ��� delete from (table) where *�� ���������� * ��ġ�� ���� �� �ִ� ���ǽ��� ��� �����մϴ�.");

		btn_delete.addActionListener(this);
		btn_cancel.addActionListener(this);

		setSize(900, 450);
		setVisible(true);

	}

	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == btn_delete) {
			sql_delete();
		}

		else
			this.dispose();

	}

	private void sql_delete() {

		String query = "";

		if (combo.getSelectedItem().toString().equals("��ȭ")) {
			query = "delete from movies where *;";
		} else if (combo.getSelectedItem().toString().equals("������")) {
			query = "delete from schedules where *;";
		} else if (combo.getSelectedItem().toString().equals("�󿵰�")) {
			query = "delete from halls where *;";
		} else if (combo.getSelectedItem().toString().equals("Ƽ��")) {
			query = "delete from tickets where *;";
		} else if (combo.getSelectedItem().toString().equals("�¼�")) {
			query = "delete from seats where *;";
		} else if (combo.getSelectedItem().toString().equals("ȸ����")) {
			query = "delete from members where *;";
		} else if (combo.getSelectedItem().toString().equals("��������")) {
			query = "delete from reservations where *;";
		}

		try {

			Statement stmt = con.createStatement();
			query = query.replaceFirst("\\*", where.getText());
			stmt.execute(query);
			printArea.setText("���� ����");

		} catch (SQLException exception) {
			printArea.setText(" ���ǽ��� �ٽ� Ȯ�����ֽñ� �ٶ��ϴ�.\n");
			printArea.append(" �ܷ�Ű�� ������� Ʃ���� ������ �Ұ����մϴ�.");
		}

	}

}

class Update extends JFrame implements ActionListener {

	String table_name[] = { "��ȭ", "������", "�󿵰�", "Ƽ��", "�¼�", "ȸ����", "��������" };

	Connection con;

	JComboBox<String> combo = new JComboBox<String>(table_name);
	JTextField set = new JTextField(65);
	JTextField where = new JTextField(65);
	JTextArea printArea = new JTextArea(15, 78);
	JButton btn_update = new JButton("����");
	JButton btn_cancel = new JButton("���");

	public Update(Connection con) {

		this.con = con;

		setTitle("����");
		
		Container c = getContentPane();
		c.setLayout(new FlowLayout());
		printArea.setEditable(false);

		JPanel inPanel1 = new JPanel();
		JPanel inPanel2 = new JPanel();

		inPanel1.add(new JLabel("Set ����"));
		inPanel1.add(set);
		inPanel2.add(new JLabel("Where ����"));
		inPanel2.add(where);

		c.add(combo);
		c.add(btn_update);
		c.add(btn_cancel);
		c.add(inPanel1);
		c.add(inPanel2);
		c.add(printArea);
		c.add(new JScrollPane(printArea));

		printArea.setText("���̺��� �����Ͻð� �� �Է� â�� ���ǽ��� �Է����ּ���.\n\n");
		printArea.append("Where ���ǹ��� ���� ��� �¼��� ������ ��츦 ���� ��� seatId = 1�� �������� �Է����ּ���.\n");
		printArea.append("�̿ܿ��� UPDATE SET ? WHERE *;�� ���������� * ��ġ�� ���� �� �ִ� ���ǽ��� ��� �����մϴ�.\n\n");
		printArea.append("Set ���ǹ��� ���� ��� �¼��� ��뿩�θ� ������ ��� SeatStatus = false�� �������� �Է����ּ���.\n");
		printArea.append("�̿ܿ��� UPDATE SET ? WHERE *;�� ���������� ? ��ġ�� ���� �� �ִ� ���ǽ��� ��� �����մϴ�.");
		

		btn_update.addActionListener(this);
		btn_cancel.addActionListener(this);

		setSize(900, 450);
		setVisible(true);

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == btn_update) {
			sql_Update();
		}

		else if (e.getSource() == btn_cancel) {
			this.dispose();
		}

	}

	private void sql_Update() {

		String query = "";

		if (combo.getSelectedItem().toString().equals("��ȭ")) {
			query = "UPDATE movies SET * WHERE *;";
		} else if (combo.getSelectedItem().toString().equals("������")) {
			query = "UPDATE schedules SET * WHERE *;";
		} else if (combo.getSelectedItem().toString().equals("�󿵰�")) {
			query = "UPDATE halls SET * WHERE *;";
		} else if (combo.getSelectedItem().toString().equals("Ƽ��")) {
			query = "UPDATE tickets SET * WHERE *;";
		} else if (combo.getSelectedItem().toString().equals("�¼�")) {
			query = "UPDATE seats SET * WHERE *;";
		} else if (combo.getSelectedItem().toString().equals("ȸ����")) {
			query = "UPDATE members SET * WHERE *;";
		} else if (combo.getSelectedItem().toString().equals("��������")) {
			query = "UPDATE reservations SET * WHERE *;";
		}

		try {

			Statement stmt = con.createStatement();

			query = query.replaceFirst("\\*", set.getText());
			query = query.replaceFirst("\\*", where.getText());

			stmt.execute(query);

			printArea.setText(" ������ �Ϸ�Ǿ����ϴ�.");

		} catch (SQLException exception) {
			printArea.setText(" ���ǽ��� �ش� ���̺� ����Ǵ��� �ٽ� Ȯ�����ֽñ� �ٶ��ϴ�.\n");
			printArea.append(" �ܷ�Ű�� ������̰ų� �ߺ��� Ű ���� ������ �������ֽʽÿ�.");
		}

	}	

}

public class TheaterDB extends JFrame implements ActionListener {

	Connection con;
	String Driver = "";
	String url = "jdbc:mysql://localhost:3306/madang?&serverTimezone=Asia/Seoul";
	String userid = "madang";
	String pwd = "madang";

	JButton btn1 = new JButton();
	JButton btn2 = new JButton();
	JOptionPane msg = new JOptionPane();

	public TheaterDB() {

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.println("����̹� �ε� ����");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {
			System.out.println("�����ͺ��̽� ���� �غ�...");
			con = DriverManager.getConnection(url, userid, pwd);
			System.out.println("�����ͺ��̽� ���� ����");			
		} catch (SQLException e) {
			e.printStackTrace();
		}

		setTitle("Mode select");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Container c = getContentPane();

		JPanel btn = new JPanel();

		btn1.setText("������");
		btn2.setText("ȸ��");

		btn1.setSize(50, 50);
		btn2.setSize(50, 50);

		btn1.addActionListener(this);
		btn2.addActionListener(this);

		btn.add(btn1);
		btn.add(btn2);

		c.add(btn);

		setSize(300, 100);
		setVisible(true);

	}	

	@Override
	public void actionPerformed(ActionEvent e) {

		try {

			if (e.getSource() == btn1) { // ������ ��� �� ȣ��
				adminF admin = new adminF(con);
			}

			else if (e.getSource() == btn2) { // ȸ�� ��� �� ȣ��, ȸ��ID 1�� �����Ͽ� ȣ��
				memberF member = new memberF(con, 1);
			}

		} catch (Exception exception) {
			msg.showMessageDialog(null, "���� ���� ���� " + exception);
		}

	}

	public static void main(String[] args) {
		TheaterDB run = new TheaterDB();
		
		run.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				try {
					run.con.close();
				} catch (Exception e4) { 	}
				System.out.println("�����ͺ��̽� ���� ����");
				System.exit(0);
			}
		});
		
	}
}
