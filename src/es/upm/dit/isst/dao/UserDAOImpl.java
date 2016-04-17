package es.upm.dit.isst.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import es.upm.dit.isst.lab.tools.Tools;
import es.upm.dit.isst.models.User;


public class UserDAOImpl implements UserDAO {
	
	private static UserDAOImpl instance;
	
	private UserDAOImpl() {
		// TODO Auto-generated constructor stub
	}
	
	public static UserDAOImpl getInstance(){
			if(instance == null)
				instance = new UserDAOImpl();
			return instance;
	}
	
	@Override
	public User createUser(String username, String email, int salt, String password, String completeName, String role,String profilePicBlobKey) {
		// TODO Auto-generated method stub
		User user = null;
		EntityManager em = EMFService.get().createEntityManager();
		//TODO Neded some logic before creation?
		user = new User(username,email,salt,password,completeName,role,profilePicBlobKey);
		em.persist(user);
		em.close();
		
		return user;
	}

	@Override
	public User getUser(String username) {
		EntityManager em = EMFService.get().createEntityManager();
		Query q = em.createQuery("select t from User t where t.username = :username");
		q.setParameter("username", username);
		User res = null;
		List<User> users = q.getResultList();
		if (users.size() > 0)
			res = (User)(q.getResultList().get(0));
		em.close();
		return res;
	}

	@Override
	public boolean validateUser(String username, String password) {
		User res = getUser(username);
		int salt = res.getSalt();
		String hash = Tools.sha256(password+salt);
		
		String hashedPassword = res.getHashedPassword();
		
		if(hash.equals(hashedPassword))
			return true;
		else
			return false;
	}

	@Override
	public User updateUser(User user) {
		// TODO Auto-generated method stub
		EntityManager em = EMFService.get().createEntityManager();
		User res = em.merge(user);
		em.close();
		return res;
	}

	@Override
	public void deleteUser(String username) {
		// TODO Auto-generated method stub
		EntityManager em = EMFService.get().createEntityManager();
		try{
			User hypUser = em.find(User.class, username);
			em.remove(hypUser);
		} finally {
			em.close();
		}
		
	}

}
