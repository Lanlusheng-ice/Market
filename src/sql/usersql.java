package sql;

import java.sql.SQLException;
import java.util.List;

import vo.user;

public interface usersql {
	public user search(String username) throws SQLException;
	public List<user> searchalluser() throws SQLException;
	public void modifypwd(String username,String pwd) throws SQLException;
	public int find(String goodname) throws SQLException;
	public void register(user user) throws SQLException ;
}
