import java.sql.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

class memberF extends JFrame implements ActionListener, MouseListener{ // 회원 선택시 출력되는 프레임
	
	Connection con; // SQL Connection 및 쿼리 동작 결과 출력
	ResultSet rs;
	
	int memberId;
	int selectedId = 0;	
	String today = "2021-03-10";

	JOptionPane msg = new JOptionPane(); // 팝업 메세지 출력용 JOptionPane
	
	JTextField inputTitle = new JTextField(12); // 검색용 입력 폼
	JTextField inputDirector = new JTextField(12);
	JTextField inputActor = new JTextField(12);
	JTextField inputGenre = new JTextField(12);
	JTextField selectedMovie = new JTextField(10);
	JButton btn_movies = new JButton("조회"); // 검색 버튼
	JButton btn_reserve = new JButton("예매"); // 예매 버튼
	JButton btn_inquiry = new JButton("예매 조회"); // 예매 조회 버튼
	
	String[] movies_property = {"MovieId", "영화명", "상영시간", "상영등급", "감독명", "배우명", "장르", "영화소개", "개봉일"}; 
	DefaultTableModel model = new DefaultTableModel(movies_property, 0) {		// JTable로 출력할 Model
		@Override
		public boolean isCellEditable(int row, int column) { // 편집 기능 제거
			return false;
		}
	};
	
	JTable resultTable;
	
	public memberF(Connection con, int memberId) { // 생성자 및 폼 생성
		
		this.con = con;
		this.memberId = memberId;
		
		setTitle("회원");
		
		Container c = getContentPane();
		c.setLayout(new FlowLayout());
				
		JPanel inputForm = new JPanel();
		
		inputForm.add(new JLabel("영화명"));
		inputForm.add(inputTitle);
		inputForm.add(new JLabel("    감독명"));
		inputForm.add(inputDirector);
		inputForm.add(new JLabel("    배우명"));
		inputForm.add(inputActor);
		inputForm.add(new JLabel("    장르"));
		inputForm.add(inputGenre);
		inputForm.add(btn_movies);		
		
		JPanel reservePan = new JPanel();
		selectedMovie.setEditable(false); // 선택된 영화 제목 출력 폼 편집 기능 제거		
		
		reservePan.add(btn_reserve);
		reservePan.add(btn_inquiry);
		reservePan.add(new JLabel(" 회원 ID: "+memberId));
		reservePan.add(new JLabel(" /  오늘 날짜: "+today));
		reservePan.add(new JLabel(" / 선택된 영화 "));
		reservePan.add(selectedMovie);

		resultTable = new JTable(model);
		JScrollPane rsSP = new JScrollPane(resultTable);
		rsSP.setPreferredSize(new Dimension(850, 300));		
		
		c.add(inputForm);
		c.add(reservePan);
		c.add(rsSP);
				
		btn_movies.addActionListener(this); // 조회 버튼
		btn_reserve.addActionListener(this); // 예매 버튼
		btn_inquiry.addActionListener(this); // 예매 조회 버튼
		
		resultTable.addMouseListener(this); // 조회 결과 클릭 시 이벤트 삽입
		
		setSize(900, 450);
		setVisible(true);
		
	}
	
	public void actionPerformed(ActionEvent e) { // 버튼 클릭시 동작 삽입
		
		if(e.getSource() == btn_movies) { // 영화 검색 조회
			select_movie();
		}
		
		else if(e.getSource() == btn_reserve) { // 예매 버튼
			
			if(selectedId == 0) { // 영화를 선택하지 않았을 시 알림창 출력
				msg.showMessageDialog(null, "영화를 선택해주세요.");
				return;
			}
			
			Reserve reserve = new Reserve(con, selectedId, memberId, today); // 예매 폼 호출
		}
		
		else if(e.getSource() == btn_inquiry) {
			myReservation myreserve = new myReservation(con, memberId, today); // 나의 예매 내역 폼 호출
		}
		
	}
	
	@Override
	public void mouseClicked(MouseEvent e) { // JTable 클릭 시 정보 최신화
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
	
	private void select_movie() { // 검색 조회 기능

		JOptionPane msg = new JOptionPane();
		String query = "";
		boolean notFirst = false;
		model.setNumRows(0);
		
		try {
			
			query = "Select * from Movies where "; // 쿼리문 초기화
			
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
			rs = stmt.executeQuery(query); // 쿼리문 실행 후 결과를 반환받아 저장
			
			while(rs.next()) { // JTable에 삽입
				
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
			msg.showMessageDialog(null, "검색 키워드를 입력해주세요.");
		}
		
	}	
	
}

class Reserve extends JFrame implements ActionListener, MouseListener{ // 예매 폼
	
	Connection con;
	JOptionPane msg = new JOptionPane(); 
	
	String today;
	String screenDay;
	int memberId;
	int movieId;
	int scheduleId;
	int hallId;
	int sellingPrice = 12000;
	
	String[] method = {"카드", "현금"};
	String[] DC = {"카드 할인", "통신사 할인", "없음"};
	JComboBox<String> Payment = new JComboBox<String>(method);
	JComboBox<String> discount = new JComboBox<String>(DC);
	JButton btn_reserve = new JButton("예매");
	JButton btn_cancel = new JButton("취소");
	JTextField selectedSched = new JTextField(10); // 선택한 상영 일정을 출력할 폼
	
	String[] schedules_property = {"ScheduleId", "상영관", "날짜", "요일","시간"}; 
	DefaultTableModel model = new DefaultTableModel(schedules_property, 0) { // 선택한 영화의 상영 일정을 저장할 model		
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};
	
	JTable schedulesTable = new JTable(model);
	
	public Reserve(Connection con, int movieId, int memberId, String today) { // 생성자 및 폼 생성
		
		this.con = con;
		this.memberId = memberId; 
		this.movieId = movieId;
		this.today = today;
		
		setTitle("예매");
		
		Container c = getContentPane();
		c.setLayout(new FlowLayout());
		
		selectedSched.setEditable(false); // 선택된 상영 일정 출력창의 편집 기능 제거
		
		JPanel selectPanel = new JPanel(); // 컴포넌트들을 그룹화할 JPanel 생성
		JPanel btnPanel = new JPanel();
		
		selectPanel.add(new JLabel("회원아이디: " + memberId)); // JPanel에 컴포넌트들 삽입
		selectPanel.add(new JLabel("/ 오늘: " + today));			
		selectPanel.add(new JLabel("/ 결제 수단  "));
		selectPanel.add(Payment);
		selectPanel.add(new JLabel("  할인 선택  "));
		selectPanel.add(discount);
		btnPanel.add(btn_reserve);
		btnPanel.add(btn_cancel);
		btnPanel.add(new JLabel("선택된 상영 일정"));		
		btnPanel.add(selectedSched);
		
		c.add(selectPanel); // 프레임에 삽입
		c.add(btnPanel);
		
		JScrollPane rsSP = new JScrollPane(schedulesTable);
		rsSP.setPreferredSize(new Dimension(850, 300));	
		
		select_Schedules(); // 상영 일정 조회
		
		c.add(rsSP);
		
		schedulesTable.addMouseListener(this); // JTable과 버튼에 이벤트리스너 삽입
		btn_reserve.addActionListener(this);
		btn_cancel.addActionListener(this);
		
		setSize(900, 450);
		setVisible(true);
		
	}
	
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == btn_reserve) { // 예매 버튼을 눌렀을 때 
									
			try { // 현재 날짜와 상영 일정의 날짜를 비교하여 상영일이 지났으면 알림창 출력
				String date1 = today;
				String date2 = screenDay;	
				
				if(date1.compareTo(date2) > 0) {
					msg.showMessageDialog(null, "이미 상영 종료된 일정입니다.");		
					return;
				}
				
			} catch (Exception e1) { // 상영 일정이 선택되지 않은 경우 예외 처리
				msg.showMessageDialog(null, "상영 일정을 선택해주세요.");
				return;
			}			
			
			make_Reserve(); // 예매 기능 호출	
		
		}
		
		else if(e.getSource() == btn_cancel) { // 취소 선택 시 예매 창 종료
			dispose();			
		}
		
	}
		
	@Override
	public void mouseClicked(MouseEvent e) { // 마우스로 Table 클릭 시 정보 최신화
		int row = schedulesTable.getSelectedRow();
		scheduleId = (int) schedulesTable.getModel().getValueAt(row, 0);
		hallId = (int) schedulesTable.getModel().getValueAt(row, 1);	
		screenDay = schedulesTable.getModel().getValueAt(row, 2).toString();
		selectedSched.setText("" + scheduleId + "번");
	}	
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}	
	@Override
	public void mouseExited(MouseEvent e) {}
	
	private void select_Schedules() { // 상영 일정 조회 기능
		
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
			msg.showMessageDialog(null, "쿼리 연결 오류");
		}		
		
	}
	
	private void make_Reserve() { // 예매 기능
		
		String query = "";
		ResultSet count;
		int reserveId = 0;
		int ticketId = 0;
		int seatId = 0;
		
		if(hallId == 0) {
			msg.showMessageDialog(null, "상영 일정을 선택해주세요.");
			return;
		}		
		
		try {
			
			Statement stmt = con.createStatement();
			
			count = stmt.executeQuery("select max(reservationid) from reservations;");
			count.next();
			reserveId = count.getInt(1) + 1;
			
			if(discount.getSelectedItem().toString().equals("없음")) {				
				query = "insert into reservations values(" + reserveId + ", '" + Payment.getSelectedItem().toString() + "', '결제대기', 12000, "+ memberId +", str_to_date('" + today +  "', '%Y-%m-%d'));";
			}
			
			else {
				query = "insert into reservations values(" + reserveId + ", '" + Payment.getSelectedItem().toString() + "', '결제대기', 9000, "+ memberId +", str_to_date('" + today +  "', '%Y-%m-%d'));";			
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
			msg.showMessageDialog(null, "예매가 완료되었습니다.");
			
		}catch(SQLException exception) {
			msg.showMessageDialog(null, "상영 일정을 선택해주세요.");
		}
	}
	
}

class myReservation extends JFrame implements ActionListener, MouseListener{ // 나의 예매 내역 폼
	
	Connection con;
	ResultSet rs;
	
	JOptionPane msg = new JOptionPane(); 
	JButton delete = new JButton("삭제");
	JButton change_movie = new JButton("영화 변경");
	JButton change_sche = new JButton("일정 변경");
	JButton close = new JButton("닫기");
	JTextField selectedReserve = new JTextField(10); // 선택한 상영 일정을 출력할 폼
		
	String title;
	String today;
	int memberId;	
	int ticketId = 0;
	int seatId;
	int reservationId;
	int movieId;
	
	String[] myReserve_property = {"티켓번호", "영화명", "상영일", "상영관번호", "좌석번호", "판매가격"}; 
	DefaultTableModel model = new DefaultTableModel(myReserve_property, 0) { // 선택한 영화의 상영 일정을 저장할 model		
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};
	
	JTable myReserveTable = new JTable(model);	
	
	public myReservation(Connection con, int memberId, String today) { // 나의 예매 내역 폼 생성자
		
		this.con = con;
		this.memberId = memberId;
		this.today = today;
		
		setTitle("나의 예매 내역");
		
		Container c = getContentPane();
		c.setLayout(new FlowLayout());
		
		JScrollPane rsSP = new JScrollPane(myReserveTable);
		rsSP.setPreferredSize(new Dimension(850, 300));	
		
		selectedReserve.setEditable(false);
		
		select_myReservation(); // 상영 일정 조회
		
		JPanel upper = new JPanel();
		
		upper.add(new JLabel("회원ID: " + memberId));
		upper.add(new JLabel(" / 선택된 티켓 번호: "));
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
	public void mouseClicked(MouseEvent e) { // 마우스로 Table 클릭 시 정보 최신화
		int row = myReserveTable.getSelectedRow();
		ticketId = (int)myReserveTable.getModel().getValueAt(row, 0);	
		title = myReserveTable.getModel().getValueAt(row, 1).toString();	
		ShowInformation show = new ShowInformation(con, ticketId);	 // 클릭한 예매 내역에 대한 상세 정보 출력 폼 호출	
		
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
		
		if(e.getSource() == close) // 닫기 버튼 클릭 시 페이지 닫기
			this.dispose();
		
		else if(e.getSource() == delete) { // 예매 내역 삭제 버튼 클릭 시 삭제 후 테이블 최신화
			delete_Reservation();
			select_myReservation();
		}

		else if(e.getSource() == change_movie) { // 영화 변경 버튼 클릭 시 영화 변경 폼 호출
			if(ticketId == 0) { // 선택된 예매 내역이 없을 경우 메세지 표시
				msg.showMessageDialog(null, "변경할 예매 내역을 선택해주세요.");
				return;
			}
			ChangeMovie chmv = new ChangeMovie(con, movieId, ticketId, seatId, memberId, today); // 영화 변경 폼 호출
			select_myReservation(); // 테이블 최신화
		}
		
		else if(e.getSource() == change_sche) {	// 일정 변경 버튼 클릭 시 일정 변경 폼 호출	
			if(movieId == 0) { // 선택된 예매 내역이 없을 경우 메세지 표시
				msg.showMessageDialog(null, "변경할 예매 내역을 선택해주세요.");
				return;
			}			
			ChangeSchedule cs = new ChangeSchedule(con, movieId, ticketId, seatId, memberId, today);// 상영 일정 변경 폼 호출
			select_myReservation(); // 테이블 최신화
		}
		
	}
	
	private void select_myReservation() { // 나의 예매 내역을 DB로부터 불러오는 메소드
		
		String query;
		model.setNumRows(0); // 테이블 초기화
		
		try {
			
			Statement stmt = con.createStatement();
			query = "select s2.ticketId, movies.title, schedules.openingDate, s2.Hallid, s2.seatid, s2.sellingprice\r\n" // 본인이 예매한 영화에 대한 정보를 탐색하는 쿼리문
					+ "from movies, schedules, \r\n"
					+ "(select ticketId, scheduleid, hallid, seatid, sellingprice\r\n"
					+ "from tickets, (select reservationid from reservations where memberId = "
					+ memberId + ") s1 \r\n"
					+ "where tickets.reservationid = s1.Reservationid) s2\r\n"
					+ "where s2.scheduleid = schedules.ScheduleId and movies.Movieid = schedules.MovieId;";
			
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next()) { // 수행 결과를 테이블에 입력
				
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
			msg.showMessageDialog(null, "쿼리 연결 오류");
		}		
	}
	
	private void delete_Reservation() { // 예매 내역 삭제 메소드
		
		String query;
		
		try {			
			
			if(ticketId == 0) throw new Exception(); // 선택된 예매 내역이 없을 경우 예외 처리
			
			Statement stmt = con.createStatement();
			
			query = "delete from tickets where ticketId = " + ticketId + ";"; // 티켓 삭제 쿼리문 수행
			stmt.execute(query);
			
			query = "update seats set seatStatus = false where seatId = " + seatId + ";"; // 좌석 상태 미사용으로 변경
			stmt.execute(query);
			
			query = "delete from reservations where reservationId = " + reservationId + ";"; // 예매 내역 삭제
			stmt.execute(query);	
			
			msg.showMessageDialog(null, "삭제가 완료되었습니다.");
			
		}catch(Exception exception) {
			msg.showMessageDialog(null, "삭제할 예매 내역을 선택해주세요.");			
		}
		
	}
		
}

class ShowInformation extends JFrame implements ActionListener{ // 예매 정보를 클릭하면 상세 정보를 표시해 주는 폼
	
	Connection con;
	ResultSet rs;
	String query;
	
	int ticketId;
	int scheduleId;
	int hallId;

	JTextArea printArea = new JTextArea(19, 78);
	JButton close = new JButton("닫기");
	
	public ShowInformation(Connection con, int ticketId) { // 상세 정보 표시 폼 생성자
		
		this.con = con;
		this.ticketId = ticketId;
		
		setTitle("예매 내역 상세 조회");
		
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
			
			query = "Select * from Schedules where scheduleId = " +scheduleId+ ";"; // 상영일정 정보 조회
			rs=stmt.executeQuery(query);
			printArea.append("상영일정\nScheduleId\tMovieId\tHallId\tOpeningDay\tDayofweek\tTimes\tStartTime\n");
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
						
	  	  	query = "Select * from Halls where hallId = " + hallId + ";"; // 상영관 정보 조회
			rs=stmt.executeQuery(query);
			printArea.append("상영관\nHallId\tnumSeat\tHallStatus\n");
			while(rs.next()) {
	  	  		printArea.append(""+rs.getInt(1));
	  	  		printArea.append("\t"+rs.getInt(2));
	  	  		printArea.append("\t"+rs.getBoolean(3)+"\n");	  	  		
			}	  	  	 
			printArea.append("\n");
			
			query = "Select * from Tickets where ticketId = " + ticketId + ";"; // 티켓 정보 조회
			rs=stmt.executeQuery(query);
			printArea.append("티켓\nTicketId\tScheduleId\tHallId\tSeatId\tReservationId\tIssueStatus\tPrice\tSellingPrice\n");
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
			printArea.setText("SQL 연결 오류");
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

class ChangeMovie extends JFrame implements ActionListener, MouseListener { // 영화 변경 폼
	
	Connection con;
	ResultSet rs;
	
	int selectedId = 0;
	int ticketId;
	int memberId;
	int seatId;
	int currentMovieId;
	String today;
	
	JOptionPane msg = new JOptionPane(); // 팝업 메세지 출력용 JOptionPane
	
	JTextField inputTitle = new JTextField(12); // 검색용 입력 폼
	JTextField inputDirector = new JTextField(12);
	JTextField inputActor = new JTextField(12);
	JTextField inputGenre = new JTextField(12);
	JTextField selectedMovie = new JTextField(10);
	JButton btn_movies = new JButton("조회"); // 검색 버튼
	JButton btn_reserve = new JButton("선택"); // 예매 버튼
	JButton btn_close = new JButton("취소"); // 취소 버튼 
	
	String[] movies_property = {"MovieId", "영화명", "상영시간", "상영등급", "감독명", "배우명", "장르", "영화소개", "개봉일"}; 
	DefaultTableModel model = new DefaultTableModel(movies_property, 0) {		// JTable로 출력할 Model
		@Override
		public boolean isCellEditable(int row, int column) { // 편집 기능 제거
			return false;
		}
	};
	
	JTable resultTable;
	
	public ChangeMovie(Connection con, int movieId, int ticketId, int seatId, int memberId, String today) { // 영화 변경 폼 생성자
		
		this.con = con;
		this.ticketId = ticketId;
		this.memberId = memberId;
		this.today = today;
		this.seatId = seatId;
		this.currentMovieId = movieId;
		
		setTitle("영화 변경");
		
		Container c = getContentPane();
		c.setLayout(new FlowLayout());
		
		JPanel inputForm = new JPanel();
		
		inputForm.add(new JLabel("영화명")); 
		inputForm.add(inputTitle);
		inputForm.add(new JLabel("    감독명"));
		inputForm.add(inputDirector);
		inputForm.add(new JLabel("    배우명"));
		inputForm.add(inputActor);
		inputForm.add(new JLabel("    장르"));
		inputForm.add(inputGenre);
		inputForm.add(btn_movies);		
		
		JPanel reservePan = new JPanel();
		selectedMovie.setEditable(false); // 선택된 영화 제목 출력 폼 편집 기능 제거		
		
		reservePan.add(btn_reserve);
		reservePan.add(btn_close);
		reservePan.add(new JLabel(" 회원 ID: "+memberId));
		reservePan.add(new JLabel(" /  오늘 날짜: "+today));
		reservePan.add(new JLabel(" / 선택된 영화 "));
		reservePan.add(selectedMovie);

		resultTable = new JTable(model);
		JScrollPane rsSP = new JScrollPane(resultTable);
		rsSP.setPreferredSize(new Dimension(850, 300));		
		
		c.add(inputForm);
		c.add(reservePan);
		c.add(rsSP);
				
		btn_movies.addActionListener(this); // 조회 버튼
		btn_reserve.addActionListener(this); // 예매 버튼
		btn_close.addActionListener(this); // 취소 버튼
		
		resultTable.addMouseListener(this); // 조회 결과 클릭 시 이벤트 삽입
				
		setSize(900, 450);
		setVisible(true);
		
	}
	
	public void actionPerformed(ActionEvent e) { // 버튼 클릭시 동작 삽입
		
		if(e.getSource() == btn_movies) { // 영화 검색 조회
			select_movie();
		}
		
		else if(e.getSource() == btn_reserve) { // 예매 버튼
			
			if(selectedId == 0) { // 영화를 선택하지 않았을 시 알림창 출력
				msg.showMessageDialog(null, "영화를 선택해주세요.");
				return;
			}			
			
			if(selectedId == currentMovieId) { // 변경하려는 영화가 현재 예매 내역과 같은 영화일 경우 메세지 표시
				msg.showMessageDialog(null, "현재 예매내역과 같은 영화입니다.\n일정 변경을 원하시면 이전 페이지에서 일정 변경을 선택해주세요.");
				return;
			}		

			ChangeSchedule cs = new ChangeSchedule(con, selectedId, ticketId, seatId, memberId, today);// 상영일정 선택 폼 호출

		}
		
		else if(e.getSource() == btn_close)
			this.dispose();
		
	}
	
	@Override
	public void mouseClicked(MouseEvent e) { // JTable 클릭 시 정보 최신화
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
	
	private void select_movie() { // 검색 조회 기능

		JOptionPane msg = new JOptionPane();
		String query = "";
		boolean notFirst = false;
		model.setNumRows(0);	
		
		try {
			
			query = "Select * from Movies where "; // 쿼리문 초기화
			
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
			rs = stmt.executeQuery(query); // 쿼리문 실행 후 결과를 반환받아 저장
			
			while(rs.next()) { // JTable에 삽입
				
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
			msg.showMessageDialog(null, "검색 키워드를 입력해주세요.");
		}
		
	}	
	
}

class ChangeSchedule extends JFrame implements ActionListener, MouseListener { // 일정 변경 폼
	
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
	
	JButton btn_reserve = new JButton("선택");
	JButton btn_cancel = new JButton("취소");
	JTextField selectedSched = new JTextField(10); // 선택한 상영 일정을 출력할 폼
	
	String[] schedules_property = {"ScheduleId", "상영관", "날짜", "요일","시간"}; 
	DefaultTableModel model = new DefaultTableModel(schedules_property, 0) { // 선택한 영화의 상영 일정을 저장할 model		
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};
	
	JTable schedulesTable = new JTable(model);
	
	public ChangeSchedule (Connection con, int movieId, int ticketId, int seatId, int memberId, String today) { // 일정 변경 폼 생성자
		
		this.con = con;
		this.memberId = memberId; 
		this.movieId = movieId;
		this.today = today;
		this.ticketId = ticketId;
		this.seatId = seatId;
		
		setTitle("상영일정 선택");
		
		Container c = getContentPane();
		c.setLayout(new FlowLayout());
		
		selectedSched.setEditable(false); // 선택된 상영 일정 출력창의 편집 기능 제거
		
		JPanel selectPanel = new JPanel(); // 컴포넌트들을 그룹화할 JPanel 생성
		JPanel btnPanel = new JPanel();
		
		selectPanel.add(new JLabel("회원ID: " + memberId)); // JPanel에 컴포넌트들 삽입
		selectPanel.add(new JLabel("/ 오늘: " + today));		

		btnPanel.add(btn_reserve);
		btnPanel.add(btn_cancel);
		btnPanel.add(new JLabel("선택된 상영 일정"));		
		btnPanel.add(selectedSched);
		
		c.add(selectPanel); // 프레임에 삽입
		c.add(btnPanel);
		
		JScrollPane rsSP = new JScrollPane(schedulesTable);
		rsSP.setPreferredSize(new Dimension(850, 300));	
		
		select_Schedules(); // 상영 일정 조회
		
		c.add(rsSP);
		
		schedulesTable.addMouseListener(this); // JTable과 버튼에 이벤트리스너 삽입
		btn_reserve.addActionListener(this);
		btn_cancel.addActionListener(this);
		
		setSize(900, 450);
		setVisible(true);
		
	}
	
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == btn_reserve) { // 예매 버튼을 눌렀을 때 
									
			try { // 현재 날짜와 상영 일정의 날짜를 비교하여 상영일이 지났으면 알림창 출력
				String date1 = today;
				String date2 = screenDay;	
				
				if(date1.compareTo(date2) > 0) {
					msg.showMessageDialog(null, "이미 상영 종료된 일정입니다.");		
					return;
				}
				
			} catch (Exception e1) { // 상영 일정이 선택되지 않은 경우 예외 처리
				msg.showMessageDialog(null, "상영 일정을 선택해주세요.");
				return;
			}			
			
			UpdateReservation(); // 예매 기능 호출	
		
		}
		
		else if(e.getSource() == btn_cancel) { // 취소 선택 시 예매 창 종료
			this.dispose();			
		}
		
	}
		
	@Override
	public void mouseClicked(MouseEvent e) { // 마우스로 Table 클릭 시 정보 최신화
		int row = schedulesTable.getSelectedRow();
		scheduleId = (int) schedulesTable.getModel().getValueAt(row, 0);
		hallId = (int) schedulesTable.getModel().getValueAt(row, 1);	
		screenDay = schedulesTable.getModel().getValueAt(row, 2).toString();
		selectedSched.setText("" + scheduleId + "번");
	}	
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}	
	@Override
	public void mouseExited(MouseEvent e) {}
	
	private void select_Schedules() { // 상영 일정 조회 기능
		
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
			msg.showMessageDialog(null, "쿼리 연결 오류");
		}		
		
	}
	
	private void UpdateReservation() { // 일정 변경 메소드
		
		String query = "";
		ResultSet count;
		int newSeatId = 0;
				
		try {
			
			Statement stmt = con.createStatement();
			
			query = "select scheduleId from tickets where ticketId =" + ticketId +";"; // 원래 예매한 상영 일정 조회 후 저장 
			count = stmt.executeQuery(query);
			count.next();
			currentScheId = count.getInt(1);
			
			if(scheduleId == currentScheId) { // 변경하려는 상영 일정이 원래 상영 일정과 중복되면 메세지 표시
				msg.showMessageDialog(null, "현재 예매내역과 같은 상영일정입니다.\n일정 변경을 원하시면 다른 일정을 선택해주세요.");
				return;
			};
			
			query = "update seats set seatStatus = false where seatId = " + seatId + ";"; // 변경 전 좌석 사용여부 미사용으로 변경
			stmt.executeUpdate(query);			
			
			count = stmt.executeQuery("select count(seatid) from seats;");
			count.next();
			newSeatId = count.getInt(1) + 1;
			
			query = "insert into seats values(" + newSeatId + ", " + hallId + ", true);"; // 새로운 좌석 정보 등록
			stmt.executeUpdate(query);
			
			query = "UPDATE tickets SET scheduleId =" + scheduleId + " where ticketId = " + ticketId + ";"; // 티켓의 상영 일정 정보 변경
			stmt.executeUpdate(query);
			
			query = "UPDATE tickets SET hallId=" + hallId + " where ticketId = " + ticketId + ";"; // 티켓의 상영관 정보 변경
			stmt.executeUpdate(query);
			
			query = "UPDATE tickets SET seatId =" + newSeatId + " where ticketId = " + ticketId + ";"; // 티켓의 좌석 정보 변경
			stmt.executeUpdate(query);
						
			msg.showMessageDialog(null, "변경이 완료되었습니다.");
			
		}catch(SQLException exception) {
			msg.showMessageDialog(null, "상영 일정을 선택해주세요.");
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

		setTitle("관리자");

		this.con = con;

		Container c = getContentPane();
		c.setLayout(new FlowLayout());
		printArea.setEditable(false);

		btn_init = new JButton("초기화");
		btn_insert = new JButton("입력");
		btn_update = new JButton("변경");
		btn_delete = new JButton("삭제");
		btn_select = new JButton("검색");

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
			printArea.append("영화\nMovieId\tTitle\tRunningTime\tRating\tDirector\t\tActor\t\tGenre\tSynopsis\t\tReleaseDay\n");
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
			printArea.append("상영관\nHallId\tnumSeat\tHallStatus\n");
			while(rs.next()) {
	  	  		printArea.append(""+rs.getInt(1));
	  	  		printArea.append("\t"+rs.getInt(2));
	  	  		printArea.append("\t"+rs.getBoolean(3)+"\n");	  	  		
			}	  	  	 
			printArea.append("\n");
			
			query = "Select * from Schedules;";
			rs=stmt.executeQuery(query);
			printArea.append("상영일정\nScheduleId\tMovieId\tHallId\tOpeningDay\tDayofweek\tTimes\tStartTime\n");
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
			printArea.append("회원\nMemberId\tMemberName\tPhone\t\tEmail\n");
			while(rs.next()) {
	  	  		printArea.append(""+rs.getInt(1));
	  	  		printArea.append("\t"+rs.getString(2));
	  	  		printArea.append("\t"+rs.getString(3));
	  	  		printArea.append("\t\t"+rs.getString(4)+"\n");	  	  		
			}	  	  	 
			printArea.append("\n");
	  	  	
			query = "Select * from Reservations;";
			rs=stmt.executeQuery(query);
			printArea.append("예매정보\nReservationId\tPayment\tPayStatus\tPrice\tMemberId\tPayDate\n");
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
			printArea.append("좌석\nSeatId\tHallId\tSeatStatus\n");
			while(rs.next()) {
	  	  		printArea.append(""+rs.getInt(1));
	  	  		printArea.append("\t"+rs.getInt(2));
	  	  		printArea.append("\t"+rs.getBoolean(3)+"\n");	  	  		
			}	  	  	 
			printArea.append("\n");
			
			query = "Select * from Tickets;";
			rs=stmt.executeQuery(query);
			printArea.append("티켓\nTicketId\tScheduleId\tHallId\tSeatId\tReservationId\tIssueStatus\tPrice\tSellingPrice\n");
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
			printArea.setText("SQL 연결 오류");
		} 
		
	}
	
}

class Initial extends JFrame implements ActionListener {

	Connection con;
	JButton btn_yes;
	JButton btn_no;
	JOptionPane msg;

	public Initial(Connection con) {

		setTitle("초기화");

		this.con = con;
		msg = new JOptionPane();

		Container c = getContentPane();
		c.setLayout(new BorderLayout());

		btn_yes = new JButton("Yes");
		btn_no = new JButton("No");

		JLabel notice = new JLabel("DB를 초기화 하시겠습니까?");
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
					"insert into movies values(1, '범죄도시2', 020000, 15, '이상용', '마동석', '범죄, 액션', '금천서 강력반은 베트남으로 도주한 용의자를 인도받아 오라는 미션을 받는다.', str_to_date('2021-01-14', '%Y-%m-%d'));");
			stmt.execute(
					"insert into movies values(2, '시네마천국', 020400, 0, '쥬세페토르나토레', '필립느와레', '드라마', '재개봉, 영화인 토토의 이야기', str_to_date('2021-02-22', '%Y-%m-%d'));\r\n");
			stmt.execute(
					"insert into movies values(3, '아이언맨', 020500, 12, '존파브로', '로버트다우니주니어', 'SF, 액션', '하이테크 슈퍼 히어로의 탄생!', str_to_date('2021-03-30', '%Y-%m-%d'));");
			stmt.execute(
					"insert into movies values(4, '쇼생크탈출', 022200, 15, '프랭크다라본트', '팀로빈스', '드라마', '두려움은 너를 죄수로 가두고 희망은 너를 자유롭게 하리라', str_to_date('2021-04-24', '%Y-%m-%d'));");
			stmt.execute(
					"insert into movies values(5, '클래식', 021200, 12, '곽재용', '손예진', '멜로', '우연히 그러나 반드시, 잊혀진 약속이 깨어났다.', str_to_date('2021-05-30', '%Y-%m-%d'));");
			stmt.execute(
					"insert into movies values(6, '타이타닉', 031400, 15, '제임스카메론', '레오나르도디카프리오', '멜로', '영원으로 기억될 세기의 러브 스토리', str_to_date('2021-06-01', '%Y-%m-%d'));");
			stmt.execute(
					"insert into movies values(7, '레옹', 021200, 19, '뤽베송', '장르노', '범죄, 액션', '나도 행복해 지고 싶어. 잠도 자고, 뿌리도 내릴거야.', str_to_date('2021-07-11', '%Y-%m-%d'));");
			stmt.execute(
					"insert into movies values(8, '살인의추억', 021200, 15, '봉준호', '송강호', '범죄', '미치도록 잡고 싶었습니다. 당신은 누구십니까', str_to_date('2021-08-25', '%Y-%m-%d'));");
			stmt.execute(
					"insert into movies values(9, '쥬라기월드', 022700, 12, '콜린트레보로우', '크리스프랫', '액션, 모험', '이제 모든 것이 끝난다.', str_to_date('2021-09-01', '%Y-%m-%d'));");
			stmt.execute(
					"insert into movies values(10, '우상', 022400, 15, '이수진', '한석규', '드라마', '아들의 사고로 정치 인생 최악의 위기를 맞게 된 남자', str_to_date('2021-11-20', '%Y-%m-%d'));");

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
					"insert into schedules values(1, 1, 1, str_to_date('2021-01-14', '%Y-%m-%d'), '목', 8, 093000);");
			stmt.execute(
					"insert into schedules values(2, 2, 3, str_to_date('2021-03-01','%Y-%m-%d'), '월', 6, 082000);");
			stmt.execute(
					"insert into schedules values(3, 3, 2, str_to_date('2021-04-01', '%Y-%m-%d'), '목', 10, 075000);");
			stmt.execute(
					"insert into schedules values(4, 3, 3, str_to_date('2021-04-01', '%Y-%m-%d'), '목', 9, 081000);");
			stmt.execute(
					"insert into schedules values(5, 4, 7, str_to_date('2021-04-28', '%Y-%m-%d'), '수', 7, 100000);");
			stmt.execute(
					"insert into schedules values(6, 5, 4, str_to_date('2021-06-01', '%Y-%m-%d'), '화', 8, 091000);");
			stmt.execute(
					"insert into schedules values(7, 6, 5, str_to_date('2021-06-01', '%Y-%m-%d'), '화', 7, 085000);");
			stmt.execute(
					"insert into schedules values(8, 7, 6, str_to_date('2021-07-11', '%Y-%m-%d'), '일', 6, 101000);");
			stmt.execute(
					"insert into schedules values(9, 8, 8, str_to_date('2021-08-26', '%Y-%m-%d'), '목', 7, 110000);");
			stmt.execute(
					"insert into schedules values(10, 9, 10, str_to_date('2021-09-12', '%Y-%m-%d'), '일', 8, 091500);");
			stmt.execute(
					"insert into schedules values(11, 10, 9, str_to_date('2021-11-20', '%Y-%m-%d'), '토', 6, 101000);");

			stmt.execute("insert into members values(1, '유재석', '010-1234-5678', 'qwerty@naver.com');");
			stmt.execute("insert into members values(2, '박명수', '010-2143-6587', 'asdfgh@daum.net');");
			stmt.execute("insert into members values(3, '정준하', '010-9876-5432', 'zxcv@gmail.com');");
			stmt.execute("insert into members values(4, '정형돈', '010-5678-9000', 'dodo@naver.com');");
			stmt.execute("insert into members values(5, '노홍철', '010-7777-1234', 'hong123@gmail.com');");
			stmt.execute("insert into members values(6, '하동훈', '010-4232-5342', 'haha77@naver.com');");
			stmt.execute("insert into members values(7, '길성준', '010-2233-4321', 'lee@daum.net');");
			stmt.execute("insert into members values(8, '김태호', '010-6541-6512', 'teo123@gmail.com');");
			stmt.execute("insert into members values(9, '제영재', '010-4569-4412', 'young33@gmail.com');");
			stmt.execute("insert into members values(10, '제임스', '010-9123-4012', 'james00@gmail.com');");

			stmt.execute(
					"insert into reservations values(1, '카드', '결제완료', 9000, 3, str_to_date('2021-01-14', '%Y-%m-%d'));");
			stmt.execute(
					"insert into reservations values(2, '현금', '결제완료', 12000, 6, str_to_date('2021-01-15', '%Y-%m-%d'));");
			stmt.execute(
					"insert into reservations values(3, '카드', '결제완료', 12000, 5, str_to_date('2021-03-02', '%Y-%m-%d'));");
			stmt.execute(
					"insert into reservations values(4, '카드', '결제완료', 9000, 1, str_to_date('2021-04-09', '%Y-%m-%d'));");
			stmt.execute(
					"insert into reservations values(5, '카드', '결제완료', 9000, 1, str_to_date('2021-06-03', '%Y-%m-%d'));");
			stmt.execute(
					"insert into reservations values(6, '카드', '결제완료', 12000, 1, str_to_date('2021-06-15', '%Y-%m-%d'));");
			stmt.execute(
					"insert into reservations values(7, '현금', '결제완료', 8000, 4, str_to_date('2021-07-20', '%Y-%m-%d'));");
			stmt.execute(
					"insert into reservations values(8, '카드', '결제완료', 12000, 2, str_to_date('2021-08-27', '%Y-%m-%d'));");
			stmt.execute(
					"insert into reservations values(9, '카드', '결제대기', 9000, 1, str_to_date('2021-09-08', '%Y-%m-%d'));");
			stmt.execute(
					"insert into reservations values(10, '현금', '결제대기', 12000, 9, str_to_date('2021-09-10', '%Y-%m-%d'));");
						
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

			msg.showMessageDialog(null, "초기화 완료");
		} catch (SQLException exception) {
			msg.showMessageDialog(null, "쿼리 연결 오류");
		}

	}

}

class Insert extends JFrame implements ActionListener {

	String table_name[] = { "영화", "상영일정", "상영관", "티켓", "좌석", "회원고객", "예매정보" };

	Connection con;
	JTextField writeArea = new JTextField(65);
	JComboBox<String> combo = new JComboBox<String>(table_name);
	JTextArea printArea = new JTextArea(15, 78);
	JButton btn1 = new JButton("저장");
	JButton btn2 = new JButton("취소");

	public Insert(Connection con) {

		setTitle("입력");

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

		printArea.setText(" 새로운 데이터를 입력하시려면 항목을 선택하시고 위의 입력창에 데이터를 입력한 뒤 저장 버튼을 눌러주세요.\n");
		printArea.append(" 각 속성은 /로 구분해주시고 날짜는 yyyy-mm-dd 형식으로 입력해주세요. 상영등급은 정수로 입력해주시고 전체이용가는 0을 입력해주세요.\n");
		printArea.append(" 사용유무, 발권여부의 경우 true 또는 false로 입력해주시고 시간은 hhmmss 형식으로 입력해주세요.\n\n");
		printArea.append(" 영화 입력 예시 : 1/범죄도시2/020000/15/이상용/마동석/범죄, 액션/영화 소개 입력/2021-01-14\n\n");
		printArea.append(" 영화 입력 시 : 영화번호/영화명/상영시간/상영등급/감독/배우/장르/영화소개/개봉일\n");
		printArea.append(" 상영일정 입력 시 : 상영일정번호/영화번호/상영관번호/상영시작익/상영요일/상영회차/상영시작시간\n");
		printArea.append(" 상영관 입력 시 : 상영관번호/좌석수/상영관사용여부\n");
		printArea.append(" 티켓 입력 시 : 티켓번호/상영일정번호/상영관번호/좌석번호/예매번호/발권여부/표준가격/판매가격\n");
		printArea.append(" 좌석 입력 시 : 좌석번호/상영관번호/좌석사용여부\n");
		printArea.append(" 회원고객 입력 시 : 회원아이디/고객명/휴대폰번호/전자메일주소\n");
		printArea.append(" 예매정보 입력 시 : 예매번호/결제방법/결제상태/결제금액/회원아이디/결제일자\n");
		
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

		if (combo.getSelectedItem().toString().equals("영화")) {
			query = "insert into movies values(*, '*', *, *, '*', '*', '*', '*', str_to_date('*', '%Y-%m-%d'));";
		} else if (combo.getSelectedItem().toString().equals("상영일정")) {
			query = "insert into schedules values(*, *, *, str_to_date('*', '%Y-%m-%d'), '*', *, *);";
		} else if (combo.getSelectedItem().toString().equals("상영관")) {
			query = "insert into halls values(*, *, *);";
		} else if (combo.getSelectedItem().toString().equals("티켓")) {
			query = "insert into tickets values(*, *, *, *, *, *, *, *);";
		} else if (combo.getSelectedItem().toString().equals("좌석")) {
			query = "insert into seats values(*, *, *);";
		} else if (combo.getSelectedItem().toString().equals("회원고객")) {
			query = "insert into members values(*, '*', '*', '*');";
		} else if (combo.getSelectedItem().toString().equals("예매정보")) {
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

			printArea.setText("입력 성공");
		} catch (SQLException e) {
			printArea.setText("입력 실패, 중복된 키 값이 포함되어 있거나 양식에 맞는 값을 입력하였는지 확인해주십시오.\n존재하지 않는 외래키의 참조는 불가능합니다.");
		} catch (NumberFormatException e) {
			printArea.setText("입력 실패, 입력값은 /로 구분해 입력해주십시오.\n");
			printArea.append("날짜는 yyyy-mm-dd 형식으로 입력해주십시오.");
		} catch (IndexOutOfBoundsException e) {
			printArea.setText("입력 실패, 입력값은 /로 구분해 입력해주십시오.\n");
		}

	}

}

class Delete extends JFrame implements ActionListener {

	String table_name[] = { "영화", "상영일정", "상영관", "티켓", "좌석", "회원고객", "예매정보" };

	Connection con;

	JComboBox<String> combo = new JComboBox<String>(table_name);
	JTextField where = new JTextField(65);
	JTextArea printArea = new JTextArea(15, 78);
	JButton btn_delete = new JButton("삭제");
	JButton btn_cancel = new JButton("취소");

	public Delete(Connection con) {

		this.con = con;

		setTitle("삭제");
		
		Container c = getContentPane();
		c.setLayout(new FlowLayout());
		printArea.setEditable(false);

		JPanel inPanel = new JPanel();

		inPanel.add(new JLabel("Where 조건"));
		inPanel.add(where);

		c.add(combo);
		c.add(btn_delete);
		c.add(btn_cancel);
		c.add(inPanel);
		c.add(printArea);
		c.add(new JScrollPane(printArea));

		printArea.setText("테이블을 선택하시고 입력 창에 조건식을 입력해주세요.\n\n");
		printArea.append("where 조건문은 티켓을 삭제할 경우를 예를 들면 ticketId = 1의 형식으로 입력해주세요.\n");
		printArea.append("이외에도 delete from (table) where *의 쿼리문에서 * 위치에 넣을 수 있는 조건식은 사용 가능합니다.");

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

		if (combo.getSelectedItem().toString().equals("영화")) {
			query = "delete from movies where *;";
		} else if (combo.getSelectedItem().toString().equals("상영일정")) {
			query = "delete from schedules where *;";
		} else if (combo.getSelectedItem().toString().equals("상영관")) {
			query = "delete from halls where *;";
		} else if (combo.getSelectedItem().toString().equals("티켓")) {
			query = "delete from tickets where *;";
		} else if (combo.getSelectedItem().toString().equals("좌석")) {
			query = "delete from seats where *;";
		} else if (combo.getSelectedItem().toString().equals("회원고객")) {
			query = "delete from members where *;";
		} else if (combo.getSelectedItem().toString().equals("예매정보")) {
			query = "delete from reservations where *;";
		}

		try {

			Statement stmt = con.createStatement();
			query = query.replaceFirst("\\*", where.getText());
			stmt.execute(query);
			printArea.setText("삭제 성공");

		} catch (SQLException exception) {
			printArea.setText(" 조건식을 다시 확인해주시기 바랍니다.\n");
			printArea.append(" 외래키로 사용중인 튜플은 삭제가 불가능합니다.");
		}

	}

}

class Update extends JFrame implements ActionListener {

	String table_name[] = { "영화", "상영일정", "상영관", "티켓", "좌석", "회원고객", "예매정보" };

	Connection con;

	JComboBox<String> combo = new JComboBox<String>(table_name);
	JTextField set = new JTextField(65);
	JTextField where = new JTextField(65);
	JTextArea printArea = new JTextArea(15, 78);
	JButton btn_update = new JButton("변경");
	JButton btn_cancel = new JButton("취소");

	public Update(Connection con) {

		this.con = con;

		setTitle("변경");
		
		Container c = getContentPane();
		c.setLayout(new FlowLayout());
		printArea.setEditable(false);

		JPanel inPanel1 = new JPanel();
		JPanel inPanel2 = new JPanel();

		inPanel1.add(new JLabel("Set 조건"));
		inPanel1.add(set);
		inPanel2.add(new JLabel("Where 조건"));
		inPanel2.add(where);

		c.add(combo);
		c.add(btn_update);
		c.add(btn_cancel);
		c.add(inPanel1);
		c.add(inPanel2);
		c.add(printArea);
		c.add(new JScrollPane(printArea));

		printArea.setText("테이블을 선택하시고 각 입력 창에 조건식을 입력해주세요.\n\n");
		printArea.append("Where 조건문은 예를 들어 좌석을 변경할 경우를 예를 들면 seatId = 1의 형식으로 입력해주세요.\n");
		printArea.append("이외에도 UPDATE SET ? WHERE *;의 쿼리문에서 * 위치에 넣을 수 있는 조건식은 사용 가능합니다.\n\n");
		printArea.append("Set 조건문은 예를 들어 좌석의 사용여부를 변경할 경우 SeatStatus = false의 형식으로 입력해주세요.\n");
		printArea.append("이외에도 UPDATE SET ? WHERE *;의 쿼리문에서 ? 위치에 넣을 수 있는 조건식은 사용 가능합니다.");
		

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

		if (combo.getSelectedItem().toString().equals("영화")) {
			query = "UPDATE movies SET * WHERE *;";
		} else if (combo.getSelectedItem().toString().equals("상영일정")) {
			query = "UPDATE schedules SET * WHERE *;";
		} else if (combo.getSelectedItem().toString().equals("상영관")) {
			query = "UPDATE halls SET * WHERE *;";
		} else if (combo.getSelectedItem().toString().equals("티켓")) {
			query = "UPDATE tickets SET * WHERE *;";
		} else if (combo.getSelectedItem().toString().equals("좌석")) {
			query = "UPDATE seats SET * WHERE *;";
		} else if (combo.getSelectedItem().toString().equals("회원고객")) {
			query = "UPDATE members SET * WHERE *;";
		} else if (combo.getSelectedItem().toString().equals("예매정보")) {
			query = "UPDATE reservations SET * WHERE *;";
		}

		try {

			Statement stmt = con.createStatement();

			query = query.replaceFirst("\\*", set.getText());
			query = query.replaceFirst("\\*", where.getText());

			stmt.execute(query);

			printArea.setText(" 변경이 완료되었습니다.");

		} catch (SQLException exception) {
			printArea.setText(" 조건식이 해당 테이블에 적용되는지 다시 확인해주시기 바랍니다.\n");
			printArea.append(" 외래키로 사용중이거나 중복된 키 값이 없도록 변경해주십시오.");
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
			System.out.println("드라이버 로드 성공");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {
			System.out.println("데이터베이스 연결 준비...");
			con = DriverManager.getConnection(url, userid, pwd);
			System.out.println("데이터베이스 연결 성공");			
		} catch (SQLException e) {
			e.printStackTrace();
		}

		setTitle("Mode select");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Container c = getContentPane();

		JPanel btn = new JPanel();

		btn1.setText("관리자");
		btn2.setText("회원");

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

			if (e.getSource() == btn1) { // 관리자 기능 폼 호출
				adminF admin = new adminF(con);
			}

			else if (e.getSource() == btn2) { // 회원 기능 폼 호출, 회원ID 1로 설정하여 호출
				memberF member = new memberF(con, 1);
			}

		} catch (Exception exception) {
			msg.showMessageDialog(null, "퀴리 연결 오류 " + exception);
		}

	}

	public static void main(String[] args) {
		TheaterDB run = new TheaterDB();
		
		run.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				try {
					run.con.close();
				} catch (Exception e4) { 	}
				System.out.println("데이터베이스 연결 종료");
				System.exit(0);
			}
		});
		
	}
}
