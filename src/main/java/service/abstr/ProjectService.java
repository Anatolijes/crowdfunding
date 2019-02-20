package service.abstr;

import model.Project;

public interface ProjectService {

    void addProject(Project project);
    Project getProjectById(Integer id);
}
