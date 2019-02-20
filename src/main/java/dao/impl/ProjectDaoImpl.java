package dao.impl;

import dao.abstr.ProjectDao;
import model.Project;
import storage.Storage;

public class ProjectDaoImpl implements ProjectDao{
	private static ProjectDaoImpl instance;

	public  static ProjectDaoImpl getInstance(){
	if (instance == null) {
		instance = new ProjectDaoImpl();
	}
            return instance;
	}

	private ProjectDaoImpl() {

	}



	@Override
	public Project getProjectById(Integer id) {
		return Storage.PROJECTS_TABLE.get(id);
	}




	@Override
	public void addProject(Project project) {
		Storage.PROJECTS_TABLE.put(project.getProjectId(), project);
	}
}
