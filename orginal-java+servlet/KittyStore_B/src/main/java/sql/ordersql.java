package sql;

import java.sql.SQLException;
import java.util.List;

import vo.order;

public interface ordersql {
	public void add(order order) throws SQLException;
	public void remove(int orderid,String username) throws SQLException;
	public void deletegood(int goodid) throws SQLException;
	public void deleteorder(int orderid) throws SQLException;
	public void modifystate(int orderid,int tostate) throws SQLException;
	public int searchid(int orderid) throws SQLException;
	public List<order> showall2(String buyername) throws SQLException ;
	public List<order> showall(int userid) throws SQLException;
	public int searchstate(int orderid) throws SQLException;
	public order getOrderById(int orderId) throws SQLException;
}
