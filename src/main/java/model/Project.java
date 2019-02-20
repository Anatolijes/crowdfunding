package model;

import java.time.LocalDateTime;

public class Project {

	private String name;
	private String description;
	private LocalDateTime createDate;
	private double allSum;
	private double currentSum;
	private String photo;
	private int projectId;
	private Integer authorId;
	private String docxAdress;

	public int getAuthorId() {
		return authorId;
	}

	public void setAuthorId(int authorId) {
		this.authorId = authorId;
	}

	public String getAuthorSurname() {
		return authorSurname;
	}

	public void setAuthorSurname(String authorSurname) {
		this.authorSurname = authorSurname;
	}

	private String authorSurname;


	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public String getTitle() {
		return name;
	}

	public void setTitle(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDateTime getCreateDate() {
		return createDate;
	}

	public void setCreateDate(LocalDateTime createDate) {
		this.createDate = createDate;
	}

	public double getAllSum() {
		return allSum;
	}

	public void setAllSum(double projectSumm) {
		this.allSum = projectSumm;
	}

	public double getCurrentSum() {
		return currentSum;
	}

	public void setCurrentSum(double projectCurrentSumm) {
		this.currentSum = projectCurrentSumm;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAuthorId(Integer authorId) {
		this.authorId = authorId;
	}

	public String getDocxAdress() {
		return docxAdress;
	}

	public void setDocxAdress(String docxAdress) {
		this.docxAdress = docxAdress;
	}
}
