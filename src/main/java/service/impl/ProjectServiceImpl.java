package service.impl;

import dao.abstr.ProjectDao;
import dao.impl.ProjectDaoImpl;
import model.Project;
import service.abstr.ProjectService;

public class ProjectServiceImpl implements ProjectService {


	private static ProjectServiceImpl instance;


	private ProjectDao projectDao = ProjectDaoImpl.getInstance();

	public static ProjectServiceImpl getInstance() {
		if (instance == null) {
			instance = new ProjectServiceImpl();
		}
		return instance;
	}

	@Override
	public void addProject(Project project) {
		projectDao.addProject(project);
	}

	@Override
	public Project getProjectById(Integer id) {
		return projectDao.getProjectById(id);
	}
}
