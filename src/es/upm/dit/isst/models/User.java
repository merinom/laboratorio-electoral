package es.upm.dit.isst.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.google.appengine.datanucleus.annotations.Unowned;

@Entity
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String username;
	private String email;
	private int salt;
	private String password;
	private String completeName;
	private String role;
	private String profilePicBlobKey;
	
	private ArrayList<String> workgroupIds = new ArrayList<>();

	@Transient
	private List<Workgroup> workgroups = new ArrayList<Workgroup>();

	public User(String username, String email, int salt, String password, String completeName, String role,
			String profilePicBlobKey) {
		super();
		this.username = username;
		this.email = email;
		this.salt = salt;
		this.password = password;
		this.completeName = completeName;
		this.role = role;
		this.profilePicBlobKey = profilePicBlobKey;
	}

	public int getSalt() {
		return salt;
	}

	public String getHashedPassword() {
		return password;
	}

	public List<Workgroup> getWorkgroups() {
		return workgroups;
	}

	public void setWorkgroups(List<Workgroup> workgroups) {
		this.workgroups = workgroups;
	}
	
	public List<String> getWorkgroupIds() {
		return workgroupIds;
	}

	public void setWorkgroupIds(ArrayList<String> workgroupIds) {
		this.workgroupIds = workgroupIds;
	}

	public String getUsername() {
		return username;
	}
	
	


	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}


	public void setEmail(String newEmail) {
		this.email = newEmail;
		
	}

	public void setPassword(String newPass) {
		this.password = newPass;
	}

	public void setImg(String profilePicKey) {
		this.profilePicBlobKey = profilePicKey;
		
	}

	
}
