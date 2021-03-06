package es.upm.dit.isst;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.*;


import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

import es.upm.dit.isst.dao.UserDAOImpl;
import es.upm.dit.isst.dao.WorkgroupDAO;
import es.upm.dit.isst.dao.WorkgroupDAOImpl;
import es.upm.dit.isst.lab.tools.Tools;
import es.upm.dit.isst.models.Permission;
import es.upm.dit.isst.models.User;
import es.upm.dit.isst.models.Workgroup;

@SuppressWarnings("serial")
public class RegisterServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.sendError(404);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		
		BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
		Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);
		List<BlobKey> blobKeys = blobs.get("profilePic");
		 
		String profilePicKey = null;
		 if(blobKeys!=null)
			profilePicKey = blobKeys.size()>0? blobKeys.get(0).getKeyString() : null;
		
			String username = req.getParameter("username");
			String password = req.getParameter("password");
			String password2 = req.getParameter("password2");
			String completeName = req.getParameter("completeName");
			String role = req.getParameter("role");
			String email = req.getParameter("email");
			
	        if(username!=null&&password!=null&&completeName!=null&&role!=null&&email!=null){
	        	
	        	if(!password.equals(password2)){
	        		resp.sendError(403);
	        	}else{
	        	
		        	if(UserDAOImpl.getInstance().getUser(username)==null){
		        		int salt = (int) (Math.random()*Integer.MAX_VALUE);
		        		String hash = Tools.sha256(password+salt);
		        		
		        		User newUser = new User(username, email, salt, hash, completeName, role,profilePicKey);
		        		Workgroup personal = new Workgroup("My own projects", newUser.getUsername(), true);
		        		newUser.getWorkgroups().add(personal);
		        		personal.getMembers().add(newUser);
		        		Permission newPerm = new Permission();
		    			newPerm.setAddMember(true);
		    			newPerm.setDeleteMember(true);
		    			newPerm.setDeleteMessage(true);
		    			newPerm.setDeleteSimulations(true);
		    			Map permMap = new HashMap<String,Permission>();
		    			permMap.put(username, newPerm);
		    			personal.setPermissions(permMap);
		        		WorkgroupDAOImpl.getInstance().createWorkgroup(personal);
		        		UserDAOImpl.getInstance().createUser(newUser);
		        		System.out.println("New user created, username: "+username);
		        		
		        		session.setAttribute("user", username);
		        		
		        		resp.setStatus(200);
		        	}else{
		        		resp.sendError(403);
		        	}
	        	}	
	        }else{
	        	resp.sendError(400);
	        }
       
	}
	
}
