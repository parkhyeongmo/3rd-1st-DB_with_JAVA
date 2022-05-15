import java.io.*;
import java.sql.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class JC17011492M extends JFrame {
	
   Connection con;
   
   String table_name[] = {"Doctors", "Nurses", "Patients", "Treatments", "Charts"};
   
   JTextArea printArea = new JTextArea(15,78);
   JTextField writeArea = new JTextField(65);
   JComboBox<String> combo = new JComboBox<String>(table_name);
   
   public JC17011492M() {
     String Driver="";
     String url="jdbc:mysql://localhost:3306/hospital?&serverTimezone=Asia/Seoul"; 
     String userid="hospital";
     String pwd="hospital";
     
     try {
       Class.forName("com.mysql.cj.jdbc.Driver");   
       System.out.println("드라이버 로드 성공");
       printArea.append("드라이버 로드 성공\n");
     } catch(ClassNotFoundException e) {
         e.printStackTrace();
      }
     
     try {
       System.out.println("데이터베이스 연결 준비...");
       printArea.append("데이터베이스 연결 준비...\n");
       con=DriverManager.getConnection(url, userid, pwd);
       System.out.println("데이터베이스 연결 성공");
       printArea.append("데이터베이스 연결 성공\n");
       printArea.append("데이터 입력의 경우 입력값은 /로 구분해 입력해주십시오.\n");
       printArea.append("Doctors 선택시 :doc_id/major_treat/doc_name/doc_gen/doc_phone/doc_email/doc_position\n");
       printArea.append("Nurses 선택시 :nur_id/major_job/nur_name/nur_gen/nur_phone/nur_email/nur_position\n");
       printArea.append("Patients 선택시 :pat_id/nur_id/doc_id/pat_name/pat_gen/pat_jumin/pat_addr/pat_phone/pat_email/pat_job\n");
       printArea.append("Treatments 선택시 :treat_id/pat_id/doc_id/treat_contents/treat_date (진료 날짜는 yyyy-mm-dd 형식으로 입력해주십시오.)\n");
       printArea.append("Charts 선택시 :chart_id/treat_id/doc_id/pat_id/nur_id/chart_contents\n");
     } catch(SQLException e) {
         e.printStackTrace();
       }
     
     setTitle("17011492/박형모");
     setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     Container c = getContentPane();
     c.setLayout(new FlowLayout());
			
     JButton btn1 = new JButton("초기화");
     JButton btn2 = new JButton("입력");
     JButton btn3 = new JButton("검색-모든테이블");
     JButton btn4 = new JButton("검색1");
     JButton btn5 = new JButton("검색2");
     JButton btn6 = new JButton("검색3");
		
     JLabel inform1 = new JLabel("입력");
     JLabel inform2 = new JLabel("출력");
     
     JPanel inPanel = new JPanel();
     JPanel outPanel = new JPanel();
     
     inPanel.add(inform1);
     inPanel.add(combo);
     inPanel.add(writeArea);
     
     outPanel.add(inform2);
     outPanel.add(printArea);
	
     btn1.addActionListener(new ActionListener() { // 초기화
    	 @Override
    	 public void actionPerformed(ActionEvent e) {			
    		 sqlClear();    		 
    	 }	
     });
     
     btn2.addActionListener(new ActionListener() { // 입력
    	 @Override
    	 public void actionPerformed(ActionEvent e) {		    		 
    		 sqlAdd();
    	 }	
     });
     
     btn3.addActionListener(new ActionListener() { // 검색 - 모든테이블
    	 @Override
    	 public void actionPerformed(ActionEvent e) {			
    		 sqlRun();    		 
    	 }	
     });
		
     btn4.addActionListener(new ActionListener() { // 검색1
    	 @Override
    	 public void actionPerformed(ActionEvent e) {			
    		 sqlSearch1();    		 
    	 }	
     });
     
     btn5.addActionListener(new ActionListener() { // 검색2
    	 @Override
    	 public void actionPerformed(ActionEvent e) {			
    		 sqlSearch2();
    	 }	
     });
     
     btn6.addActionListener(new ActionListener() { // 검색3
    	 @Override
    	 public void actionPerformed(ActionEvent e) {			
    		 sqlSearch3();
    	 }	
     });
     
		
     c.add(btn1);
     c.add(btn2);
     c.add(btn3);
     c.add(btn4);
     c.add(btn5);
     c.add(btn6);
		     
     c.add(inPanel);
     c.add(outPanel);
     
     c.add(new JScrollPane(printArea));
		
     setSize(900, 450);
     setVisible(true);
     
   }
// 접속 객체 con을 DriverManager.getConnection 함수로 생성한다. 
// 접속이 성공하면 "데이터베이스 연결 성공"을 출력하도록 한다.  
// 문자열 query에 수행할 SQL 문을 입력한다.
   
   private void sqlClear() { // 초기화 기능 메소드
	   
	   printArea.setText("초기화");
	   
	   Statement stmt = null;
	   
	   try {
			System.out.println("Clear");
			stmt = con.createStatement(); // SQL문 처리용 Statement 객체 생성
			
			stmt.executeUpdate("drop table charts");
			stmt.executeUpdate("drop table treatments");
			stmt.executeUpdate("drop table patients;");
			stmt.executeUpdate("drop table doctors;");
			stmt.executeUpdate("drop table nurses;");

			stmt.executeUpdate("CREATE TABLE Doctors (\r\n"
					+ "  doc_id INTEGER NOT NULL,\r\n"
					+ "  major_treat VARCHAR(25) NOT NULL,\r\n"
					+ "  doc_name VARCHAR(20) NOT NULL,\r\n"
					+ "  doc_gen VARCHAR(1) NOT NULL,\r\n"
					+ "  doc_phone VARCHAR(15) NULL,\r\n"
					+ "  doc_email VARCHAR(50) UNIQUE,\r\n"
					+ "  doc_position VARCHAR(20) NOT NULL\r\n"
					+ ");");
			
			stmt.executeUpdate("CREATE TABLE Nurses (\r\n"
					+ "  nur_id INTEGER NOT NULL,\r\n"
					+ "  major_job VARCHAR(25) NOT NULL,\r\n"
					+ "  nur_name VARCHAR(20) NOT NULL,\r\n"
					+ "  nur_gen char(1) NOT NULL,\r\n"
					+ "  nur_phone VARCHAR(15) NULL,\r\n"
					+ "  nur_email VARCHAR(50) UNIQUE,\r\n"
					+ "  nur_position VARCHAR(20) NOT NULL\r\n"
					+ ");");
			
			stmt.executeUpdate("CREATE TABLE Patients (\r\n"
					+ "  pat_id INTEGER NOT NULL,\r\n"
					+ "  nur_id INTEGER NOT NULL,\r\n"
					+ "  doc_id INTEGER NOT NULL,\r\n"
					+ "  pat_name VARCHAR(20) NOT NULL,\r\n"
					+ "  pat_gen VARCHAR(1) NOT NULL,\r\n"
					+ "  pat_jumin VARCHAR(14) NOT NULL,\r\n"
					+ "  pat_addr VARCHAR(100) NOT NULL,\r\n"
					+ "  pat_phone VARCHAR(15) NULL,\r\n"
					+ "  pat_email VARCHAR(50) UNIQUE,\r\n"
					+ "  pat_job VARCHAR(20) NOT NULL\r\n"
					+ ");");
			
			stmt.executeUpdate("CREATE TABLE Treatments (\r\n"
					+ "  treat_id INTEGER NOT NULL,\r\n"
					+ "  pat_id INTEGER NOT NULL,\r\n"
					+ "  doc_id INTEGER NOT NULL,\r\n"
					+ "  treat_contents VARCHAR(1000) NOT NULL,\r\n"
					+ "  treat_date DATE NOT NULL\r\n"
					+ ");");
			
			stmt.executeUpdate("CREATE TABLE Charts (\r\n"
					+ "  chart_id VARCHAR(20) NOT NULL,\r\n"
					+ "  treat_id INTEGER NOT NULL,\r\n"
					+ "  doc_id INTEGER NOT NULL,\r\n"
					+ "  pat_id INTEGER NOT NULL,\r\n"
					+ "  nur_id INTEGER NOT NULL,\r\n"
					+ "  chart_contents VARCHAR(1000) NOT NULL\r\n"
					+ ");");
			
			stmt.executeUpdate("INSERT INTO doctors VALUES (980312, '소아과', '이태정', 'M', '010-333-1340', 'ltj@hanbit.com', '과장');");
			stmt.executeUpdate("INSERT INTO doctors VALUES (000601, '내과', '안성기', 'M', '011-222-0987', 'ask@hanbit.com', '과장');");
			stmt.executeUpdate("INSERT INTO doctors VALUES (001208, '외과', '김민종', 'M', '010-333-8743', 'kmj@hanbit.com', '과장');");
			stmt.executeUpdate("INSERT INTO doctors VALUES (020403, '피부과', '이태서', 'M', '019-777-3764', 'lts@hanbit.com', '과장');");
			stmt.executeUpdate("INSERT INTO doctors VALUES (050900, '소아과', '김연아', 'F', '010-555-3746', 'kya@hanbit.com', '전문의');");
			stmt.executeUpdate("INSERT INTO doctors VALUES (050101, '내과', '차태현', 'M', '011-222-7643', 'cth@hanbit.com', '전문의');");
			stmt.executeUpdate("INSERT INTO doctors VALUES (062019, '소아과', '전지현', 'F', '010-999-1265', 'jjh@hanbit.com', '전문의');");
			stmt.executeUpdate("INSERT INTO doctors VALUES (070576, '피부과', '홍길동', 'M', '016-333-7263', 'hgd@hanbit.com', '전문의');");
			stmt.executeUpdate("INSERT INTO doctors VALUES (080543, '방사선과', '유재석', 'M', '010-222-1263', 'yjs@hanbit.com', '과장');");
			stmt.executeUpdate("INSERT INTO doctors VALUES (091001, '외과', '김병만', 'M', '010-555-3542', 'kbm@hanbit.com', '전문의');");
			
			stmt.executeUpdate("INSERT INTO nurses VALUES (050302, '소아과', '김은영', 'F', '010-555-8751', 'key@hanbit.com', '수간호사');");
			stmt.executeUpdate("INSERT INTO nurses VALUES (050021, '내과', '윤성애', 'F', '016-333-8745', 'ysa@hanbit.com', '수간호사');");
			stmt.executeUpdate("INSERT INTO nurses VALUES (040089, '피부과', '신지원', 'M', '010-666-7646', 'sjw@hanbit.com', '주임');");
			stmt.executeUpdate("INSERT INTO nurses VALUES (070605, '방사선과', '유정화', 'F', '010-333-4588', 'yjh@hanbit.com', '주임');");
			stmt.executeUpdate("INSERT INTO nurses VALUES (070804, '내과', '라하나', 'F', '010-222-1340', 'nhn@hanbit.com', '주임');");
			stmt.executeUpdate("INSERT INTO nurses VALUES (071018, '소아과', '김화경', 'F', '019-888-4116', 'khk@hanbit.com', '주임');");
			stmt.executeUpdate("INSERT INTO nurses VALUES (100356, '소아과', '이선용', 'M', '010-777-1234', 'lsy@hanbit.com', '간호사');");
			stmt.executeUpdate("INSERT INTO nurses VALUES (104145, '외과', '김현', 'M', '010-999-8520', 'kh@hanbit.com', '간호사');");
			stmt.executeUpdate("INSERT INTO nurses VALUES (120309, '피부과', '박성완', 'M', '010-777-4996', 'psw@hanbit.com', '간호사');");
			stmt.executeUpdate("INSERT INTO nurses VALUES (130211, '외과', '이서연', 'F', '010-222-3214', 'lsy2@hanbit.com', '간호사');");
			
			stmt.executeUpdate("INSERT INTO patients VALUES (2345, 050302, 980312, '안상건', 'M', 232345, '서울', '010-555-7845', 'ask@ab.com', '회사원');");
			stmt.executeUpdate("INSERT INTO patients VALUES (3545, 040089, 020403, '김성룡', 'M', 543545, '서울', '010-333-7812', 'ksn@bb.com', '자영업');");
			stmt.executeUpdate("INSERT INTO patients VALUES (3424, 070605, 080543, '이종진', 'M', 433424, '부산', '010-888-4859', 'ljj@ab.com', '회사원');");
			stmt.executeUpdate("INSERT INTO patients VALUES (7675, 100356, 050900, '최광석', 'M', 677675, '당진', '010-222-4847', 'cks@cc.com', '회사원');");
			stmt.executeUpdate("INSERT INTO patients VALUES (4533, 070804, 000601, '정한경', 'M', 744533, '강릉', '010-777-9630', 'jhk@ab.com', '교수');");
			stmt.executeUpdate("INSERT INTO patients VALUES (5546, 120309, 070576, '유원현', 'M', 765546, '대구', '016-777-0214', 'ywh@cc.com', '자영업');");
			stmt.executeUpdate("INSERT INTO patients VALUES (4543, 070804, 050101, '최재정', 'M', 454543, '부산', '010-555-4187', 'cjj@bb.com', '회사원');");
			stmt.executeUpdate("INSERT INTO patients VALUES (9768, 130211, 091001, '이진희', 'F', 119768, '서울', '010-888-3675', 'ljh@ab.com', '교수');");
			stmt.executeUpdate("INSERT INTO patients VALUES (4234, 130211, 091001, '오나미', 'F', 234234, '속초', '010-999-6541', 'onm@cc.com', '학생');");
			stmt.executeUpdate("INSERT INTO patients VALUES (7643, 071018, 062019, '송석묵', 'M', 987643, '서울', '010-222-5874', 'ssm@bb.com', '학생');");
			
			stmt.executeUpdate("INSERT INTO treatments VALUES (130516023, 2345, 980312, '감기, 몸살', STR_TO_DATE('2013-05-16','%Y-%m-%d'));");
			stmt.executeUpdate("INSERT INTO treatments VALUES (130628100, 3545, 020403, '피부 트러블 치료', STR_TO_DATE('2013-06-28','%Y-%m-%d'));");
			stmt.executeUpdate("INSERT INTO treatments VALUES (131205056, 3424, 080543, '목 디스크로 MRI 촬영', STR_TO_DATE('2013-12-05','%Y-%m-%d'));");
			stmt.executeUpdate("INSERT INTO treatments VALUES (131218024, 7675, 050900, '중이염', STR_TO_DATE('2013-12-18','%Y-%m-%d'));");
			stmt.executeUpdate("INSERT INTO treatments VALUES (131224012, 4533, 000601, '장염', STR_TO_DATE('2013-12-24','%Y-%m-%d'));");
			stmt.executeUpdate("INSERT INTO treatments VALUES (140103001, 5546, 070576, '여드름 치료', STR_TO_DATE('2014-01-03','%Y-%m-%d'));");
			stmt.executeUpdate("INSERT INTO treatments VALUES (140109026, 4543, 050101, '위염', STR_TO_DATE('2014-01-09','%Y-%m-%d'));");
			stmt.executeUpdate("INSERT INTO treatments VALUES (140226102, 9768, 091001, '화상치료', STR_TO_DATE('2014-02-26','%Y-%m-%d'));");
			stmt.executeUpdate("INSERT INTO treatments VALUES (140303003, 4234, 091001, '교통사고 외상치료', STR_TO_DATE('2014-03-03','%Y-%m-%d'));");
			stmt.executeUpdate("INSERT INTO treatments VALUES (140308087, 7643, 062019, '장염', STR_TO_DATE('2014-03-08','%Y-%m-%d'));");
			
			stmt.executeUpdate("INSERT INTO charts VALUES ('PD13572410', 130516023, 980312, 2345, 050302, '편도선, 감기약 처방');");
			stmt.executeUpdate("INSERT INTO charts VALUES ('DM11389132', 130628100, 020403, 3545, 040089, '피부약 처방');");
			stmt.executeUpdate("INSERT INTO charts VALUES ('RD10023842', 131205056, 080543, 3424, 070605, '목 디스크 의심, 추가 검사 필요');");
			stmt.executeUpdate("INSERT INTO charts VALUES ('PD13581241', 131218024, 050900, 7675, 100356, '세반고리관 추가 검사 필요');");
			stmt.executeUpdate("INSERT INTO charts VALUES ('IM12557901', 131224012, 000601, 4533, 070804, '위장약 처방');");
			stmt.executeUpdate("INSERT INTO charts VALUES ('DM11400021', 140103001, 070576, 5546, 120309, '여드름 치료제 처방');");
			stmt.executeUpdate("INSERT INTO charts VALUES ('IM12708224', 140109026, 050101, 4543, 070804, '위염 심각, 추가 검사 후 수술 권함');");
			stmt.executeUpdate("INSERT INTO charts VALUES ('GS17223681', 140226102, 091001, 9768, 130211, '화상약 처방 및 물리치료');");
			stmt.executeUpdate("INSERT INTO charts VALUES ('GS17264430', 140303003, 091001, 4234, 130211, '추가 성형수술 필요함');");
			stmt.executeUpdate("INSERT INTO charts VALUES ('PD13664611', 140308087, 062019, 7643, 071018, '장염약 처방');");
			
			printArea.append(" 성공");
			
		} catch (SQLException e) {
			System.out.println("SQL 실행 오류");
			printArea.append(" 실패");
			   
		}
	   
   }
   
   private void sqlAdd() { // 입력 기능 메소드
	   
	   printArea.setText("입력");
	   
	   String input = writeArea.getText();
	   String[] factor = input.split("/");
	   String selected_name = combo.getSelectedItem().toString();
	   	  
	   writeArea.setText("");
	   
	   Statement stmt = null;
	   
	   try {		   
			stmt = con.createStatement(); // SQL문 처리용 Statement 객체 생성
			
			if (selected_name.equals("Doctors")){		

				stmt.executeUpdate("insert into doctors values(" + Integer.parseInt(factor[0]) + ",'" + factor[1] + "','" + factor[2] + "','" + factor[3]+ "' ,'" + factor[4] + "','" + factor[5] + "','" + factor[6] + "');"); // 의사 튜플 추가				
				
			}
			
			else if(selected_name.equals("Nurses")){

				stmt.executeUpdate("insert into nurses values(" + Integer.parseInt(factor[0]) + ",'" + factor[1] + "','" + factor[2]
						+ "','" + factor[3]+ "','" + factor[4] + "','" + factor[5] + "','" + factor[6] + "');"); // 간호사 튜플 추가			
				
			}
			
			else if(selected_name.equals("Patients")){

				stmt.executeUpdate("insert into patients values(" + Integer.parseInt(factor[0]) + "," + Integer.parseInt(factor[1]) + "," 
						+ Integer.parseInt(factor[2])+ ",'" + factor[3]+ "' ,'" + factor[4] + "'," + Integer.parseInt(factor[5]) + ",'" + factor[6]
						+"','" + factor[7] + "','" + factor[8] + "','"+ factor[9] +"');"); // 환자 튜플 추가							
				
			}
			
			else if(selected_name.equals("Treatments")){
				stmt.executeUpdate("insert into treatments values("+ Integer.parseInt(factor[0]) +","+ Integer.parseInt(factor[1]) + ","
						+ Integer.parseInt(factor[2]) +",'"+ factor[3] + "', STR_TO_DATE('" + factor[4] + "','%Y-%m-%d'));"); // 진료 튜플 추가			
			}
			
			else if(selected_name.equals("Charts")){
				stmt.executeUpdate("insert into charts values('" + factor[0]+"'," + Integer.parseInt(factor[1]) + "," + Integer.parseInt(factor[2]) + "," 
						+ Integer.parseInt(factor[3]) + "," + Integer.parseInt(factor[4]) + ",'" + factor[5] + "');");
			}
			
			printArea.append(" 성공");
			
		} catch (SQLException e) {
			System.out.println("SQL 실행 오류");
			printArea.append(" 실패, 중복된 키 값이 포함되어 있는지 확인해주십시오.");
		} catch (NumberFormatException e) {
			System.out.println("입력 양식 오류");
			printArea.append(" 실패, 입력값은 /로 구분해 입력해주십시오.\n");
			
			if(selected_name.equals("Treatments")) {
				printArea.append("날짜는 yyyy-mm-dd 형식으로 입력해주십시오.");
			}
			
		} catch (IndexOutOfBoundsException e) {
			System.out.println("입력 양식 오류");
			printArea.append(" 실패, 입력값은 /로 구분해 입력해주십시오.\n");
		}
	   
	   
   }
      
   private void sqlRun() { // 검색 - 모든테이블 메소드
	  
	   printArea.setText("");
	   
  	  String query="SELECT * FROM doctors"; /* SQL 문 */
  	  try { /* 데이터베이스에 질의 결과를 가져오는 과정 */
  	  	 Statement stmt=con.createStatement();
  	  	 ResultSet rs=stmt.executeQuery(query);
  	  	 printArea.append("doc_id\tmajor_treat\tdoc_name\tdoc_gen\tdoc_phone\tdoc_email\t\tdoc_position\n");
  	  	 while(rs.next()) {
  	  		printArea.append(""+rs.getInt(1));
  	  		printArea.append("\t"+rs.getString(2));
  	  		printArea.append("\t"+rs.getString(3));
 		  	printArea.append("\t"+rs.getString(4));
 		  	printArea.append("\t"+rs.getString(5));
  	  		printArea.append("\t"+rs.getString(6));
  	  		printArea.append("\t\t"+rs.getString(7)+"\n");
  	  	 }
  	  	 
  	  	 printArea.append("\n");  	  	
  	  	 
  	  	 query = "select * from nurses";
  	  	 rs = stmt.executeQuery(query);
  	  	 printArea.append("nur_id\tmajor_job\tnur_name\tnur_gen\tnur_phone\tnur_email\t\tnur_position\n");
 	  	 while(rs.next()) {
 	  		printArea.append(""+rs.getInt(1));
 	  		printArea.append("\t"+rs.getString(2));
 	  		printArea.append("\t"+rs.getString(3));
		  	printArea.append("\t"+rs.getString(4));
		  	printArea.append("\t"+rs.getString(5));
 	  		printArea.append("\t"+rs.getString(6));
 	  		printArea.append("\t\t"+rs.getString(7)+"\n");
 	  	 }
 	  	 
 	  	 printArea.append("\n");  
  	  	 
 	  	query = "select * from patients";
 	  	 rs = stmt.executeQuery(query);
 	  	 printArea.append("pat_id\tnur_id\tdoc_id\tpat_name\tpat_gen\tpat_jumin\tpat_addr\tpat_phone\tpat_email\tpat_job\n");
	  	 while(rs.next()) {
	  		printArea.append(""+rs.getInt(1));
	  		printArea.append("\t"+rs.getInt(2));
	  		printArea.append("\t"+rs.getInt(3));
		  	printArea.append("\t"+rs.getString(4));
		  	printArea.append("\t"+rs.getString(5));
	  		printArea.append("\t"+rs.getInt(6));
	  		printArea.append("\t"+rs.getString(7));
	  		printArea.append("\t"+rs.getString(8));
	  		printArea.append("\t"+rs.getString(9));
	  		printArea.append("\t"+rs.getString(10)+"\n");
	  	 }
	  	 
	  	 printArea.append("\n");  
 	  	 
	  	 query = "select * from treatments";
	  	 rs = stmt.executeQuery(query);
	  	 printArea.append("treat_id\tpat_id\tdoc_id\ttreat_contents\t\ttreat_date\n");
	  	 while(rs.next()) {
	  		printArea.append(""+rs.getInt(1));
	  		printArea.append("\t"+rs.getInt(2));
	  		printArea.append("\t"+rs.getInt(3));
	  		printArea.append("\t"+rs.getString(4));
	  		printArea.append("\t\t"+rs.getString(5)+"\n");
	  	 }
	  	 
	  	 printArea.append("\n");  
	  	 
	  	 query = "select * from charts";
	  	 rs = stmt.executeQuery(query);
	  	 printArea.append("chart_id\ttreat_id\tdoc_id\tpat_id\tnur_id\tchart_contents\n");
	  	 while(rs.next()) {
	  		printArea.append(""+rs.getString(1));
	  		printArea.append("\t"+rs.getInt(2));
	  		printArea.append("\t"+rs.getInt(3));
	  		printArea.append("\t"+rs.getInt(4));
	  		printArea.append("\t"+rs.getInt(5));
	  		printArea.append("\t"+rs.getString(6)+"\n");
	  	 }
	  	 
	  	 printArea.append("\n");  
	  	 
  	  	 //con.close();
  	  } catch(SQLException e) {
  	  	   e.printStackTrace();
  	    }
   }     
   
   private void sqlSearch1() { // 검색1 - 김병만 의사에게 진료받은 모든 환자에 대해서 환자의 이름, 주소, 전화번호 및 진료 내역과 진료 날짜를 검색하라
	   
	   printArea.setText("");
	   
	   String query="select pat_name, pat_addr, pat_phone, t1.treat_contents, t1.treat_date \r\n"
	   		+ "from (select pat_id, treat_contents, treat_date from treatments where doc_id = 91001) t1, patients \r\n"
	   		+ "where patients.pat_id = t1.pat_id order by pat_name;"; /* SQL 문 */
	  	  try { /* 데이터베이스에 질의 결과를 가져오는 과정 */
	  	  	 Statement stmt=con.createStatement();
	  	  	 ResultSet rs=stmt.executeQuery(query);
	  	  	 printArea.append("pat_name\tpat_addr\tpat_phone\t\ttreat_contents\t\ttreat_date\n");
	  	  	 while(rs.next()) {
	  	  		printArea.append(""+rs.getString(1));
	  	  		printArea.append("\t"+rs.getString(2));
	  	  		printArea.append("\t"+rs.getString(3));
	  	  		printArea.append("\t\t"+rs.getString(4));
	  	  		printArea.append("\t\t"+rs.getString(5)+"\n");
	  	  	 }
	  	  	 
	  	  	 printArea.append("\n");	  	  	
	  	  	 
	  	  	 //con.close();
	  	  } catch(SQLException e) {
	  	  	   e.printStackTrace();
	  	    }
	   
   }
   
   private void sqlSearch2() { // 검색2 - 환자를 진료한 기록이 없는 의사에 대한 모든 정보를 검색하라
	   
	   printArea.setText("");
	   
	   String query="select * from doctors where doc_id not in (select doc_id from treatments group by doc_id);"; /* SQL 문 */
	  	  try { /* 데이터베이스에 질의 결과를 가져오는 과정 */
	  	  	 Statement stmt=con.createStatement();
	  	  	 ResultSet rs=stmt.executeQuery(query);
	  	  	 
	  	  	 printArea.append("doc_id\tmajor_treat\tdoc_name\tdoc_gen\tdoc_phone\tdoc_email\t\tdoc_position\n");
	  	  	 while(rs.next()) {
	  	  		printArea.append(""+rs.getInt(1));
	  	  		printArea.append("\t"+rs.getString(2));
	  	  		printArea.append("\t"+rs.getString(3));
	 		  	printArea.append("\t"+rs.getString(4));
	 		  	printArea.append("\t"+rs.getString(5));
	  	  		printArea.append("\t"+rs.getString(6));
	  	  		printArea.append("\t\t"+rs.getString(7)+"\n");
	  	  	 }
	  	  	 
	  	  	 printArea.append("\n");	  	  	
	  	  	 
	  	  	 //con.close();
	  	  } catch(SQLException e) {
	  	  	   e.printStackTrace();
	  	    }
	   
   }

   private void sqlSearch3() { // 검색3 - 가장 많은 환자를 진료한 의사에 대한 모든 정보를 검색하라
	   
	   printArea.setText("최대값의 진료 횟수를 가진 의사가 다수인 경우 모두 출력\n");
	   
	   String query="select d1.* \r\n"
	   		+ "from doctors d1, \r\n"
	   		+ "(select doc_id from (select doc_id, count(*) as 'treat_cnt'\r\n"
	   		+ "from treatments group by doc_id) cnt, (select max(treat_cnt) as maxi from (select doc_id, count(*) as 'treat_cnt' from treatments group by doc_id) cnt) maxx\r\n"
	   		+ "where maxx.maxi = cnt.treat_cnt) cnt1\r\n"
	   		+ "where d1.doc_id = cnt1.doc_id;"; /* SQL 문 */
	  	  try { /* 데이터베이스에 질의 결과를 가져오는 과정 */
	  	  	 Statement stmt=con.createStatement();
	  	  	 ResultSet rs=stmt.executeQuery(query);
	  	  	 
	  	  	 printArea.append("doc_id\tmajor_treat\tdoc_name\tdoc_gen\tdoc_phone\tdoc_email\t\tdoc_position\n");
	  	  	 while(rs.next()) {
	  	  		printArea.append(""+rs.getInt(1));
	  	  		printArea.append("\t"+rs.getString(2));
	  	  		printArea.append("\t"+rs.getString(3));
	 		  	printArea.append("\t"+rs.getString(4));
	 		  	printArea.append("\t"+rs.getString(5));
	  	  		printArea.append("\t"+rs.getString(6));
	  	  		printArea.append("\t\t"+rs.getString(7)+"\n");
	  	  	 }
	  	  	 
	  	  	 printArea.append("\n");	  	  	
	  	  	 
	  	  	 //con.close();
	  	  } catch(SQLException e) {
	  	  	   e.printStackTrace();
	  	    }
	   
   }
   
   
  	public static void main(String args[]) {
  	   
  		JC17011492M run=new JC17011492M();  	   

 	}
}