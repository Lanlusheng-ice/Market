package sqlimpl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import sql.goodsql;
import vo.good;

public class goodsqlimpl implements goodsql{

    Connection conn = null;

    @Override
	public boolean updateGood(int number) throws SQLException {
	    try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:D:/maoliang.db");
	        String updateQuery = "UPDATE MLgood SET number = number + ? WHERE goodid = ?";
	        PreparedStatement preparedStatement = conn.prepareStatement(updateQuery);

	        preparedStatement.setInt(1, number);
	        System.out.println("update1");
	        int rowsAffected = preparedStatement.executeUpdate();

	        preparedStatement.close();
	        conn.close();

	        return rowsAffected > 0; // 返回更新是否成功
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    return false;
	}
	@Override
	public void add(good good) throws SQLException {
		try {
			Class.forName("org.sqlite.JDBC");
	         Connection conn = DriverManager.getConnection("jdbc:sqlite:D:/maoliang.db");
	         String sql = "insert into MLgood(goodname,description,price,picture,state,number,kind,subkind,owner) values(?,?,?,?,?,?,?,?,?)";
	         PreparedStatement ps  = conn.prepareStatement(sql);
	         ps.setString(1, good.getGoodname());
	         ps.setString(2, good.getDescription());
	         ps.setDouble(3, good.getPrice());
	         ps.setString(4, good.getPicture());
	         ps.setInt(5, good.getState());
	         ps.setInt(6, good.getNumber());
	         ps.setString(7, good.getKind());
	         ps.setString(8, good.getSubkind());
	         ps.setInt(9, good.getOwner());
	         ps.executeUpdate();
	         sql = "SELECT * FROM MLgood WHERE goodname=?";//在售
	         ps = conn.prepareStatement(sql);
	         ps.setString(1, good.getGoodname());
	   		 ResultSet rs=ps.executeQuery();
	         int goodid = -1;
	         if (rs.next()) {
	             goodid = rs.getInt(1);//获得刚刚创建的id
	         }
	         ps.close();
	         String sql1 = "insert into MLhistorygood(goodid,goodname,description,price,picture,number,kind,subkind,createdate,owner) values(?,?,?,?,?,?,?,?,?,?)";
	         PreparedStatement ps1  = conn.prepareStatement(sql1);
	         ps1.setInt(1, goodid);
	         ps1.setString(2, good.getGoodname());
	         ps1.setString(3, good.getDescription());
	         ps1.setDouble(4, good.getPrice());
	         ps1.setString(5, good.getPicture());
	         ps1.setInt(6, good.getNumber());
	         ps1.setString(7, good.getKind());
	         ps1.setString(8, good.getSubkind());
	         // 获取当前时间
	         LocalDateTime currentTime = LocalDateTime.now();
	         // 定义日期时间格式
	         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	         // 格式化当前时间
	         String formattedDateTime = currentTime.format(formatter);
	         ps1.setString(9, formattedDateTime);
	         ps1.setInt(10, good.getOwner());
	         ps1.executeUpdate();
	         ps1.close();
	         conn.close();
		} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void addtocart(int goodid,int buyer) throws SQLException {
		try {
			Class.forName("org.sqlite.JDBC");
	         Connection conn = DriverManager.getConnection("jdbc:sqlite:D:/maoliang.db");
	         String sql = "insert into MLbuying(goodid,number,islike,buyer) values(?,?,?,?)";
	         PreparedStatement ps  = conn.prepareStatement(sql);
	         ps.setInt(1, goodid);
	         ps.setInt(2, 1);
	         ps.setInt(3, 0);
	         ps.setInt(4, buyer);
	         ps.executeUpdate();
	         ps.close();
	         conn.close();
		} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void addtolike(int goodid,int buyer) throws SQLException {
		try {
			Class.forName("org.sqlite.JDBC");
	         Connection conn = DriverManager.getConnection("jdbc:sqlite:D:/maoliang.db");
//	         查询表中是否已经有商品
	         String test = "select *from MLbuying where goodid = ?";
	         PreparedStatement ps  = conn.prepareStatement(test);
	         ps  = conn.prepareStatement(test);
	         ps.setInt(1, goodid);
	         ResultSet rs = ps.executeQuery();
//	         如果有，则直接修改值
	         if(rs.next()) {
	        	 String update = "update MLbuying set islike = 1 where goodid=?";
	        	 ps  = conn.prepareStatement(update);
	        	 ps.setInt(1, goodid);
	        	 ps.executeUpdate();
	         }else {
//	        	 如果没有，则直接插入一个新的
	        	 String sql = "insert into MLbuying(goodid,number,islike,buyer) values(?,?,?,?)";
		         ps  = conn.prepareStatement(sql);
		         ps.setInt(1, goodid);
		         ps.setInt(2, 0);
		         ps.setInt(3, 1);
		         ps.setInt(4, buyer);
		         ps.executeUpdate();
	         }
	         ps.close();
	         conn.close();
		} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void modifybuynumber(int buyingid,int number) throws SQLException {
		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:D:/maoliang.db");
			PreparedStatement ps = null;
			String sql;
			if (number == -1) {//自增
			     sql = "UPDATE MLbuying SET number = number + 1 WHERE buyingid = ?";
			    // System.out.println("----------11111");
			     ps = conn.prepareStatement(sql);
			     ps.setInt(1, buyingid);
			} else {
			     sql = "UPDATE MLbuying SET number = ? WHERE buyingid = ?";
			     ps = conn.prepareStatement(sql);
			     ps.setInt(1, number);
			     ps.setInt(2, buyingid);
			}
			ps.executeUpdate();
			ps.close();
			conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	@Override
	public int remove(int goodid) throws SQLException {
		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:D:/maoliang.db");
	        PreparedStatement ps = null;
	        String sql = "DELETE FROM MLgood WHERE goodid = ?";
			ps = conn.prepareStatement(sql);
			ps.setInt(1, goodid);
			int rowsDeleted = ps.executeUpdate();
	        ps.close();
	        conn.close();
			return rowsDeleted;
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return 0;
	}
	

	//查找商品名是否唯一
	@Override
	public int unique(String name) throws SQLException {
		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:D:/maoliang.db");
            String sql = "SELECT * FROM MLgood WHERE state=0 and goodname =?";//在售
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
   			ResultSet rs=ps.executeQuery();
   			if(rs.next()) {
   				ps.close();
   	   			conn.close();
	        	return 0;
   	         }else {
   	        	ps.close();
   	   			conn.close();
   	            return 1;
   	         }
   			
   		} catch (SQLException e) {
   				// TODO Auto-generated catch block
   				e.printStackTrace();
   		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	@Override
	public int findcart(int goodid,int buyer) throws SQLException {
		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:D:/maoliang.db");
            String sql = "SELECT * FROM MLbuying WHERE goodid =? and buyer =?";//在售
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, goodid);
            ps.setInt(2, buyer);
   			ResultSet rs=ps.executeQuery();
   			if(rs.next()) {
   				int num=rs.getInt(1);
   				ps.close();
   	   			conn.close();
   	   			return num;
   	         }else {
   	        	ps.close();
   	   			conn.close();
	        	return -1;
   	         }
   			
   		} catch (SQLException e) {
   				// TODO Auto-generated catch block
   				e.printStackTrace();
   		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	//查找商品是否唯一
		@Override
		public int oldunique() throws SQLException {
			try {
				Class.forName("org.sqlite.JDBC");
				Connection conn = DriverManager.getConnection("jdbc:sqlite:D:/maoliang.db");
	            String sql = "SELECT * FROM MLgood WHERE state=0";
	            PreparedStatement ps = conn.prepareStatement(sql);
	   			ResultSet rs=ps.executeQuery();
	   			if(rs.next()) {
	   				ps.close();
	   	   			conn.close();
	   		        return 0;
	   	         }else {
	   	        	ps.close();
	   	   			conn.close();
	   	            return 1;
	   	         }
	   		} catch (SQLException e) {
	   				// TODO Auto-generated catch block
	   				e.printStackTrace();
	   		} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return 0;
		}
	
	@Override
	public void modifystate(int goodid,int tostate) throws SQLException {
		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:D:/maoliang.db");
            PreparedStatement ps = null;
            String sql = "UPDATE MLgood SET state = ? WHERE goodid = ?";
	        ps = conn.prepareStatement(sql);
	        ps.setInt(1, tostate);
	        ps.setInt(2, goodid);
	        ps.executeUpdate();
	        System.out.println("ippp");
	        ps.close();
	        conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	@Override
	public List<good> shownow() throws SQLException {
		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:D:/maoliang.db");
            PreparedStatement ps = null;
			String sql = "select goodid,goodname,description,price,picture,state,number,kind,subkind,owner from MLgood where state=0";
			ps=conn.prepareStatement(sql);
				
			ResultSet rs=ps.executeQuery();
			List<good> gL=new ArrayList<good>();
			good g=null;
			while(rs.next()) {
					g=new good();
					g.setGoodid(rs.getInt(1));
					g.setGoodname(rs.getString(2));
					g.setDescription(rs.getString(3));
					g.setPrice(rs.getDouble(4));
					g.setPicture(rs.getString(5));
					g.setState(rs.getInt(6));
					g.setNumber(rs.getInt(7));
					g.setKind(rs.getString(8));
					g.setSubkind(rs.getString(9));
					g.setOwner(rs.getInt(10));
					gL.add(g);
			}
	         ps.close();
	         conn.close();
			return gL;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public good search(int goodid) throws SQLException {
		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:D:/maoliang.db");
            String sql = "SELECT * FROM MLgood WHERE goodid=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, goodid);
   			ResultSet rs=ps.executeQuery();
   			good g=null;
   			if(rs.next()) {
   				g=new good();
				g.setGoodid(rs.getInt(1));
				g.setGoodname(rs.getString(2));
				g.setDescription(rs.getString(3));
				g.setPrice(rs.getDouble(4));
				g.setPicture(rs.getString(5));
				g.setState(rs.getInt(6));
				g.setNumber(rs.getInt(7));
				g.setKind(rs.getString(8));
				g.setSubkind(rs.getString(9));
				g.setOwner(rs.getInt(10));
				ps.close();
		        conn.close();
				return g;
   	         }else {
   	            return null;
   	         }
   		} catch (SQLException e) {
   				// TODO Auto-generated catch block
   				e.printStackTrace();
   		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	

	@Override
	public List<good> searchls(String keyword,String kind,int power,int userid,int ishistory) throws SQLException {
	    Connection conn = null;
	    PreparedStatement ps = null;

	    try {
	        Class.forName("org.sqlite.JDBC");
	        conn = DriverManager.getConnection("jdbc:sqlite:D:/maoliang.db");
	        
	        
	        if(1==ishistory) {
	        	String sql = "SELECT * FROM MLhistorygood WHERE goodname LIKE ? AND kind = ? AND  owner = ?";
		        ps = conn.prepareStatement(sql);
		        // 添加 '%' 来匹配任何以关键词开头的商品名称
		        ps.setString(1, keyword + "%");
		        ps.setString(2, kind);
		        ps.setInt(3, userid);
	        }
	        else {
	        	String sql = "SELECT * FROM MLgood WHERE goodname LIKE ? AND kind = ? AND owner = ?";
		        if(0==power) {//买家
		        	sql = "SELECT * FROM MLgood WHERE goodname LIKE ? AND kind= ? AND state = 0";
		        }
		        ps = conn.prepareStatement(sql);
		        // 添加 '%' 来匹配任何以关键词开头的商品名称
		        ps.setString(1, keyword + "%");
		        ps.setString(2, kind);
		        if(1==power) {
		        	ps.setInt(3, userid);
		        }
	        }
	        ResultSet rs = ps.executeQuery();
	        List<good> gL=new ArrayList<good>();
			good g=null;
			while(rs.next()) {
				g=new good();
				g.setGoodid(rs.getInt(1));
				g.setGoodname(rs.getString(2));
				g.setDescription(rs.getString(3));
				g.setPrice(rs.getDouble(4));
				g.setPicture(rs.getString(5));
				g.setState(rs.getInt(6));
				g.setNumber(rs.getInt(7));
				g.setKind(rs.getString(8));
				g.setSubkind(rs.getString(9));
				g.setOwner(rs.getInt(10));
				gL.add(g);
			}
	        ps.close();
	        conn.close();
	        return gL;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } catch (ClassNotFoundException e) {
	        e.printStackTrace();
	    }
	    return null;
	}

	@Override
	public List<good> showall(int userid) throws SQLException {
		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:D:/maoliang.db");
            PreparedStatement ps = null;
			String sql = "select * from MLgood WHERE owner = ?";
			ps=conn.prepareStatement(sql);
			ps.setInt(1, userid);
			ResultSet rs=ps.executeQuery();
			List<good> gL=new ArrayList<good>();
			good g=null;
			while(rs.next()) {
				g=new good();
				g.setGoodid(rs.getInt(1));
				g.setGoodname(rs.getString(2));
				g.setDescription(rs.getString(3));
				g.setPrice(rs.getDouble(4));
				g.setPicture(rs.getString(5));
				g.setState(rs.getInt(6));
				g.setNumber(rs.getInt(7));
				g.setKind(rs.getString(8));
				g.setSubkind(rs.getString(9));
				g.setOwner(rs.getInt(10));
				gL.add(g);
			}
			ps.close();
	        conn.close();
			return gL;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	public List<good> showhistoryall(int userid) throws SQLException {
		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:D:/maoliang.db");
            PreparedStatement ps = null;
			String sql = "select * from MLhistorygood WHERE owner = ?";
			ps=conn.prepareStatement(sql);
			ps.setInt(1, userid);
			ResultSet rs=ps.executeQuery();
			List<good> gL=new ArrayList<good>();
			good g=null;
			while(rs.next()) {
				g=new good();
				g.setGoodid(rs.getInt(1));
				g.setGoodname(rs.getString(2));
				g.setDescription(rs.getString(3));
				g.setPrice(rs.getDouble(4));
				g.setPicture(rs.getString(5));
				g.setNumber(rs.getInt(6));
				g.setKind(rs.getString(7));
				g.setSubkind(rs.getString(8));
				g.setCreatedate(rs.getString(9));
				g.setOwner(rs.getInt(10));
				gL.add(g);
			}
			ps.close();
	        conn.close();
			return gL;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}public List<good> showlike(int userid) throws SQLException {
		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:D:/maoliang.db");
            PreparedStatement ps = null;
			String sql = "select * from MLbuying WHERE buyer = ? and islike = 1";
			ps=conn.prepareStatement(sql);
			ps.setInt(1, userid);
			ResultSet rs=ps.executeQuery();
			List<good> gL=new ArrayList<good>();
			good g=null;
			while(rs.next()) {
				g=new good();
				g.setBuyingid(rs.getInt(1));
				g.setGoodid(rs.getInt(2));
				g.setNumber(rs.getInt(3));
				sql = "select * from MLgood WHERE goodid = ?";//查找对应商品
				PreparedStatement ps2=conn.prepareStatement(sql);
				ps2.setInt(1, rs.getInt(2));
				ResultSet rs2=ps2.executeQuery();
				g.setGoodname(rs2.getString(2));
				g.setPrice(rs2.getDouble(4)*rs.getInt(3));
				g.setPicture(rs2.getString(5));
				g.setState(rs2.getInt(6));
				g.setNumbermax(rs2.getInt(7));//数量上限为库存
				g.setOwner(userid);
				gL.add(g);
				ps2.close();
			}
			ps.close();
	        conn.close();
			return gL;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public List<good> showbuyerall(int userid) throws SQLException {
		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:D:/maoliang.db");
            PreparedStatement ps = null;
			String sql = "select * from MLbuying WHERE buyer = ?";
			ps=conn.prepareStatement(sql);
			ps.setInt(1, userid);
			ResultSet rs=ps.executeQuery();
			List<good> gL=new ArrayList<good>();
			good g=null;
			while(rs.next()) {
				g=new good();
				g.setBuyingid(rs.getInt(1));
				g.setGoodid(rs.getInt(2));
				g.setNumber(rs.getInt(3));
				sql = "select * from MLgood WHERE goodid = ?";//查找对应商品
				PreparedStatement ps2=conn.prepareStatement(sql);
				ps2.setInt(1, rs.getInt(2));
				ResultSet rs2=ps2.executeQuery();
				g.setGoodname(rs2.getString(2));
				g.setPrice(rs2.getDouble(4));
				g.setPicture(rs2.getString(5));
				g.setState(rs2.getInt(6));
				g.setNumbermax(rs2.getInt(7));//数量上限为库存
				g.setOwner(userid);
				gL.add(g);
				ps2.close();
			}
			ps.close();
	        conn.close();
			return gL;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void removeBuyingItem(int buyingId) throws SQLException {
	    try {
	        Class.forName("org.sqlite.JDBC");
	        Connection conn = DriverManager.getConnection("jdbc:sqlite:D:/maoliang.db");
	        String sql = "DELETE FROM MLbuying WHERE buyingid = ?";
	        PreparedStatement ps = conn.prepareStatement(sql);
	        ps.setInt(1, buyingId);
	        ps.executeUpdate();
	        ps.close();
	        conn.close();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } catch (ClassNotFoundException e) {
	        e.printStackTrace();
	    }
	}
	
	public good getGoodById(int goodId) throws SQLException {
	    try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:D:/maoliang.db");
	        String query = "SELECT * FROM MLgood WHERE goodid = ?";
	        PreparedStatement preparedStatement = conn.prepareStatement(query);
	        preparedStatement.setInt(1, goodId);

	        ResultSet resultSet = preparedStatement.executeQuery();
	        good gf = null;

	        if (resultSet.next()) {
	            gf = new good();
	            gf.setGoodid(resultSet.getInt("goodid"));
	            gf.setGoodname(resultSet.getString("goodname"));
	            gf.setDescription(resultSet.getString("description"));
	            gf.setPrice(resultSet.getDouble("price"));
	            gf.setPicture(resultSet.getString("picture"));
	            gf.setState(resultSet.getInt("state"));
	            gf.setNumber(resultSet.getInt("number"));
	            gf.setKind(resultSet.getString("kind"));
	            gf.setSubkind(resultSet.getString("subkind"));
	            gf.setOwner(resultSet.getInt("owner"));
	            // 根据数据库列名设置相应的属性
	        }

	        resultSet.close();
	        preparedStatement.close();
	        conn.close();

	        return gf;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    return null;
	}


	@Override
	public boolean updateGood(good good) throws SQLException {
	    try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:D:/maoliang.db");
	        String updateQuery = "UPDATE MLgood SET goodname = ?, description = ?, price = ?, picture = ?, state = ?, number = ?, kind = ?, subkind = ?, owner = ? WHERE goodid = ?";
	        PreparedStatement preparedStatement = conn.prepareStatement(updateQuery);

	        preparedStatement.setString(1, good.getGoodname());
	        preparedStatement.setString(2, good.getDescription());
	        preparedStatement.setDouble(3, good.getPrice());
	        preparedStatement.setString(4, good.getPicture());
	        preparedStatement.setInt(5, good.getState());
	        preparedStatement.setInt(6, good.getNumber());
	        preparedStatement.setString(7, good.getKind());
	        preparedStatement.setString(8, good.getSubkind());
	        preparedStatement.setInt(9, good.getOwner());
	        preparedStatement.setInt(10, good.getGoodid());

	        int rowsAffected = preparedStatement.executeUpdate();

	        preparedStatement.close();
	        conn.close();

	        return rowsAffected > 0; // 返回更新是否成功
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    return false;
	}

//	@Override
//	public void modifystate(int goodid, int tostate) throws SQLException {
//		// TODO Auto-generated method stub
//		
//	}

}
