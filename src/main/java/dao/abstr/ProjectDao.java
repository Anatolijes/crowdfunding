package dao.abstr;

import model.Project;

public interface ProjectDao {

	Project getProjectById(Integer id);
	void addProject(Project project);
}
