package vn.acgroup.entities;

import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class SavedWithdrawInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "savedWithdrawInfo_generator")
	@SequenceGenerator(name = "savedWithdrawInfo_generator", sequenceName = "savedWithdrawInfo_seq", allocationSize = 500)
	private Long id;

	private long user;
	private String info;
	private String type;

	private LocalDateTime created = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public long getUser() {
		return user;
	}

	public void setUser(long user) {
		this.user = user;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public LocalDateTime getCreated() {
		return created;
	}

	public void setCreated(LocalDateTime created) {
		this.created = created;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


	public SavedWithdrawInfo () {}
	
	public SavedWithdrawInfo (long user, String type, String info) {
		this.user = user;
		this.type = type;
		this.info = info;
	}

	@Override
	public String toString() {
		return "SavedWithdrawInfo [id=" + id + ", user=" + user + ", info=" + info + ", type=" + type + ", created="
				+ created + "]";
	}
	
	
	
	
}