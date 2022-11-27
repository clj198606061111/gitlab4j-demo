import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.RepositoryFile;
import org.gitlab4j.api.models.TreeItem;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GitlabApiTest {

    private String gitLabHost = "http://192.168.10.128:8099";
    private String gitLabAccessToken = "glpat-pVNgiKkJGC3AHXY45HXk";

    String namespace = "lujun.chen";
    String projectName1 = "itclj1";
    String projectName2 = "itclj2";

    private Logger logger = LoggerFactory.getLogger(GitlabApiTest.class);

    private GitLabApi gitLabApi;

    @Before
    public void initGitlabApi() {
        gitLabApi = new GitLabApi(gitLabHost, gitLabAccessToken);
    }

    @Test
    public void readFile() throws GitLabApiException {

        //从一个项目复制文件到另外一个项目
        final Project project1 = gitLabApi.getProjectApi().getProject(namespace, projectName1);
        final Project project2 = gitLabApi.getProjectApi().getProject(namespace, projectName2);
        RepositoryFile repositoryFile1 = gitLabApi.getRepositoryFileApi().getFile(project1, "1.txt", "main");
        Optional<RepositoryFile> optionalRepositoryFile1 = gitLabApi.getRepositoryFileApi().getOptionalFile(project2, "1.txt", "main");
        if (optionalRepositoryFile1.isPresent()) {
            // gitLabApi.getRepositoryFileApi().deleteFile(project2, "1.txt", "main", "msg");
            gitLabApi.getRepositoryFileApi().updateFile(project2, repositoryFile1, "main", "commit msg");
        } else {
            gitLabApi.getRepositoryFileApi().createFile(project2, repositoryFile1, "main", "test commit msg");
        }

        //gitLabApi.getRepositoryFileApi().updateFile(project2, repositoryFile1, "main", "commit msg");
        logger.info("file1={}", repositoryFile1.getDecodedContentAsString());
    }

    @Test
    public void uploadLocalFile() throws GitLabApiException {
        final Project project2 = gitLabApi.getProjectApi().getProject(namespace, projectName2);
        RepositoryFile file = new RepositoryFile();
        file.encodeAndSetContent("文件上传");
        file.setFileName("3.txt");
        file.setFilePath("3.txt");
        gitLabApi.getRepositoryFileApi().createFile(project2, file, "main", "mmm msg");
    }

    //读取文件
    private List<String> getAllFiles(final Integer projectId, final String directory, final String branch) throws GitLabApiException {
        List<String> fileNames = new ArrayList<>();
        gitLabApi.getRepositoryApi().getTree(projectId, directory, branch).forEach(file -> {
            // 如果当前是目录，则继续获取其下的文件列表
            if (file.getType().equals(TreeItem.Type.TREE)) {
                try {
                    fileNames.addAll(getAllFiles(projectId, file.getPath(), branch));
                } catch (GitLabApiException e) {
                    throw new RuntimeException(e);
                }
                return;
            }
            // 如果是文件类型，直接添加
            final String filePath = String.join("/", directory, file.getName());
            fileNames.add(filePath);
            logger.info("add file: {}", filePath);
        });
        return fileNames;
    }
}
