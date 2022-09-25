package research_exchange.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.validation.constraints.NotBlank;

import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.transfer.UploadConfiguration;
import com.oracle.bmc.objectstorage.transfer.UploadManager;
import com.oracle.bmc.objectstorage.transfer.UploadManager.UploadRequest;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import research_exchange.dto.CommenterDTO;
import research_exchange.models.Article;
import research_exchange.models.Author;
import research_exchange.models.ExpertReview;
import research_exchange.models.Link;
import research_exchange.models.Tag;
import research_exchange.models.User;
import research_exchange.repositories.ArticleRepository;
import research_exchange.repositories.AuthorRepository;
import research_exchange.repositories.ExpertReviewRepository;
import research_exchange.repositories.LinkRepository;
import research_exchange.repositories.TagAbstractRepository;
import research_exchange.repositories.UserRepository;

@Controller("/articles")
@Secured(SecurityRule.IS_ANONYMOUS)
@ExecuteOn(TaskExecutors.IO)
public class ArticleController {

    @Inject
    ObjectStorageClient objectStorageClient;

    private final ArticleRepository articleRepository;

    private final TagAbstractRepository tagAbstractRepository;

    private final UserRepository userRepository;

    private final AuthorRepository authorRepository;

    private final LinkRepository linkRepository;

    private final ExpertReviewRepository expertReviewRepository;

    public ArticleController(ArticleRepository articleRepository, TagAbstractRepository tagAbstractRepository,
            UserRepository userRepository, AuthorRepository authorRepository, LinkRepository linkRepository,
            ExpertReviewRepository expertReviewRepository) {
        this.articleRepository = articleRepository;
        this.tagAbstractRepository = tagAbstractRepository;
        this.userRepository = userRepository;
        this.authorRepository = authorRepository;
        this.linkRepository = linkRepository;
        this.expertReviewRepository = expertReviewRepository;
    }

    @Get(value = "/{id}")
    public Optional<Article> viewAPost(@NotBlank Long id) throws Exception {
        Optional<Article> a = articleRepository.findById(id);
        Article article = null;
        if (a.isPresent()) {
            article = a.get();
            boolean isApproved = article.getApproved() == 1;

            List<Author> authorsList = new ArrayList<>();
            List<Tag> tagsList = tagAbstractRepository.findAllByArticleId(id);
            if (isApproved) {
                authorsList = authorRepository.findAllByArticleId(id);
            }
            List<Link> linksList = linkRepository.findAllByArticleId(id);
            List<ExpertReview> expertReviewsList = expertReviewRepository
                    .findAllByIdIsNotNullAnd_CommentPost_ArticleId(article.getId());

            if (tagsList != null && !tagsList.isEmpty()) {
                List<String> tags = new ArrayList<>();
                for (Tag tag : tagsList) {
                    tags.add(tag.getName());
                }
                article.setTags(tags);
            }
            if (isApproved) {
                List<CommenterDTO> authors = new ArrayList<>();
                for (Author author : authorsList) {
                    String username = author.getUsername();
                    Optional<User> user = userRepository.findByUsername(username);
                    String name = user.isPresent() ? user.get().getName() : null;
                    authors.add(new CommenterDTO(username, name));
                }
            }
            if (linksList != null && !linksList.isEmpty()) {
                List<String> links = new ArrayList<>();
                for (Link link : linksList) {
                    links.add(link.getUrl());
                }
                article.setLinks(links);
            }
            if (expertReviewsList != null && !expertReviewsList.isEmpty()) {
                List<String> expertReviews = new ArrayList<>();
                for (ExpertReview expertReview : expertReviewsList) {
                    expertReviews.add(expertReview.getStatus());
                }
                article.setExpertReviews(expertReviews);
            }
        }

        if (article == null) {
            throw new Exception("No article found matching given ID.");
        }

        return a;
    }

    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Post(value = "/", consumes = MediaType.MULTIPART_FORM_DATA)
    public HttpResponse<String> addArticle(Authentication authentication, CompletedFileUpload articlePdf, String title,
            String articleAbstract,
            String authors, @Nullable String tags, String mainField, String subField, @Nullable String repoUrl,
            @Nullable String dataUrl, @Nullable String extraLinks) {

        List<String> authorNames = Arrays.asList(authors.split(","));
        if (!authorNames.contains(authentication.getName())) {
            return HttpResponse.badRequest("Your name must be included in the list of author names");
        }

        for (String author : authorNames) {
            if (userRepository.findByUsername(author).isEmpty()) {
                return HttpResponse.badRequest("Every author must be the username of an existing user");
            }
        }

        final String bucketName = "research-exchange-pdf-storage-bucket";
        final String namespaceName = "idvpzhveofap";

        final String filename = articlePdf.getFilename();

        if (filename.length() < 4 || !filename.substring(filename.length() - 4).equals(".pdf")) {
            return HttpResponse.badRequest("Filename should end with .pdf");
        }

        final String randomUuid = UUID.randomUUID().toString();
        final String objectName = filename.substring(0, filename.length() - 4) + "-" + randomUuid + ".pdf";

        UploadConfiguration uploadConfiguration = UploadConfiguration.builder().allowMultipartUploads(true)
                .allowParallelUploads(true).build();

        UploadManager uploadManager = new UploadManager(objectStorageClient, uploadConfiguration);

        PutObjectRequest request = PutObjectRequest.builder().bucketName(bucketName).namespaceName(namespaceName)
                .objectName(objectName).build();

        InputStream stream = null;
        try {
            stream = articlePdf.getInputStream();
        } catch (IOException ex) {
            return HttpResponse.badRequest("File was unable to be read");
        }

        UploadRequest uploadDetails = UploadRequest.builder(stream, articlePdf.getSize()).allowOverwrite(true)
                .build(request);

        uploadManager.upload(uploadDetails);

        final String pdfUrl = "https://objectstorage.us-ashburn-1.oraclecloud.com/n/" + namespaceName + "/b/"
                + bucketName + "/o/" + objectName;

        Article a = new Article(title, articleAbstract, mainField, subField, pdfUrl, repoUrl, dataUrl);
        Article articleSaved = articleRepository.save(a);

        if (tags != null && !tags.equals("")) {
            String[] tagNames = tags.split(",");
            for (String t : tagNames) {
                Tag tagEntry = new Tag(t, articleSaved);
                tagAbstractRepository.save(tagEntry);
            }
        }

        for (String au : authorNames) {

            Author authorEntry = new Author(au, articleSaved);
            authorRepository.save(authorEntry);
        }

        if (extraLinks != null && !extraLinks.equals("")) {
            String[] linkNames = extraLinks.split(",");
            for (String l : linkNames) {
                Link linkEntry = new Link(l, articleSaved);
                linkRepository.save(linkEntry);
            }
        }

        return HttpResponse.ok(articleSaved.getId().toString());
    }

    @Get(value = "{?searchValue,mainField,subField,tags}")
    public List<Article> viewArticlesByTag(@Nullable String searchValue, @Nullable String mainField,
            @Nullable String subField, @Nullable @QueryValue List<String> tags, int pageNum, int recordsPerPage,
            boolean approved) {
        List<Article> retArticles = new ArrayList<>();

        List<ExpertReview> allExpertReviews = expertReviewRepository.findAll();

        // for search query specifying tags
        if (tags != null && !tags.isEmpty()) {
            for (String tag : tags) {
                boolean matchesWithTags = true;
                boolean nameMatchWithTags = false;
                List<Tag> tagList = tagAbstractRepository.findByName(tag);
                for (Tag t : tagList) {
                    Article a = t.getArticle();
                    boolean isApproved = a.getApproved() == 1;
                    if (searchValue != null && !searchValue.equals("")) {
                        if (a.getName().contains(searchValue)) {
                            nameMatchWithTags = true;
                        }
                    }
                    if (searchValue != null && !searchValue.equals("") && nameMatchWithTags == false) {
                        List<Author> authorsMatch = authorRepository.findAllByArticleId(a.getId());
                        for (Author authorInMatchList : authorsMatch) {
                            if (authorInMatchList.getUsername().contains(searchValue)) {
                                matchesWithTags = true;
                                break;
                            }
                            matchesWithTags = false;
                        }
                    }
                    if (mainField != null && !mainField.equals("")) {
                        matchesWithTags = matchesWithTags && a.getMainField().equals(mainField);
                    }
                    if (subField != null && !subField.equals("")) {
                        matchesWithTags = matchesWithTags && a.getSubField().equals(subField);
                    }
                    if (matchesWithTags) {
                        List<Author> authorsList = new ArrayList<>();
                        List<Tag> tagsList = tagAbstractRepository.findAllByArticleId(a.getId());
                        if (isApproved) {
                            authorsList = authorRepository.findAllByArticleId(a.getId());
                        }

                        List<String> reviewStatuses = new LinkedList<>();
                        for (ExpertReview er : allExpertReviews) {
                            if (er.getCommentPost().getArticle().getId().equals(a.getId())) {
                                reviewStatuses.add(er.getStatus());
                            }
                        }

                        if (tagList != null && !tagList.isEmpty()) {
                            List<String> tagNames = new ArrayList<>();
                            for (Tag tagObject : tagsList) {
                                tagNames.add(tagObject.getName());
                            }
                            a.setTags(tagNames);
                        }
                        if (isApproved) {
                            List<CommenterDTO> authors = new ArrayList<>();
                            for (Author author : authorsList) {
                                String username = author.getUsername();
                                Optional<User> user = userRepository.findByUsername(username);
                                String name = user.isPresent() ? user.get().getName() : null;
                                authors.add(new CommenterDTO(username, name));
                            }
                            a.setAuthors(authors);
                        }
                        a.setExpertReviews(reviewStatuses);

                        if (approved) {
                            if (isApproved) {
                                retArticles.add(a);
                            }
                        } else {
                            retArticles.add(a);
                        }
                    }
                }
            }

            // for search query not specifying tags
        } else {
            List<Article> articleList = articleRepository.findAll();
            for (Article article : articleList) {
                boolean isApproved = article.getApproved() == 1;
                boolean matchesWithoutTags = true;
                boolean nameMatch = false;
                if (searchValue != null && !searchValue.equals("")) {
                    if (article.getName().contains(searchValue)) {
                        nameMatch = true;
                    }
                }
                if (searchValue != null && !searchValue.equals("") && nameMatch == false) {
                    List<Author> authorsMatch = authorRepository.findAllByArticleId(article.getId());
                    for (Author authorInMatchList : authorsMatch) {
                        if (authorInMatchList.getUsername().contains(searchValue)) {
                            matchesWithoutTags = true;
                            break;
                        }
                        matchesWithoutTags = false;
                    }
                }
                if (mainField != null && !mainField.equals("")) {
                    matchesWithoutTags = matchesWithoutTags && article.getMainField().equals(mainField);
                }
                if (subField != null && !subField.equals("")) {
                    matchesWithoutTags = matchesWithoutTags && article.getSubField().equals(subField);
                }
                if (matchesWithoutTags) {
                    List<Author> authorsList = new ArrayList<>();
                    List<Tag> tagsList = tagAbstractRepository.findAllByArticleId(article.getId());
                    if (isApproved) {
                        authorsList = authorRepository.findAllByArticleId(article.getId());
                    }

                    List<String> reviewStatuses = new LinkedList<>();
                    for (ExpertReview er : allExpertReviews) {
                        if (er.getCommentPost().getArticle().getId().equals(article.getId())) {
                            reviewStatuses.add(er.getStatus());
                        }
                    }

                    if (tagsList != null && !tagsList.isEmpty()) {
                        List<String> tagNames = new ArrayList<>();
                        for (Tag tagObject : tagsList) {
                            tagNames.add(tagObject.getName());
                        }
                        article.setTags(tagNames);
                    }
                    if (isApproved) {
                        List<CommenterDTO> authors = new ArrayList<>();
                        for (Author author : authorsList) {
                            String username = author.getUsername();
                            Optional<User> user = userRepository.findByUsername(username);
                            String name = user.isPresent() ? user.get().getName() : null;
                            authors.add(new CommenterDTO(username, name));
                        }
                        article.setAuthors(authors);
                    }
                    article.setExpertReviews(reviewStatuses);
                    if (approved) {
                        if (isApproved) {
                            retArticles.add(article);
                        }
                    } else {
                        retArticles.add(article);
                    }
                }
            }
        }

        if (retArticles.isEmpty()) {
            return new ArrayList<>();
        }

        int startIndex = pageNum * recordsPerPage;
        int endIndex = Math.min(startIndex + recordsPerPage, retArticles.size());

        if (startIndex >= retArticles.size()) {
            throw new IndexOutOfBoundsException("Requested page number is out of bounds.");
        }
        return retArticles.subList(startIndex, endIndex);
    }

    @Get(value = "/numTotal")
    public long getTotalNumArticles() {
        return articleRepository.count();
    }

    @Get(value = "/{username}/approved")
    public List<Article> viewApprovedArticlesForUser(@NotBlank String username) {
        List<Article> approvedArticles = new ArrayList<>();
        List<Author> authors = authorRepository.findByUsername(username);
        for (Author author : authors) {
            Article a = author.getArticle();
            if (a.getApproved() == 1) {
                List<Tag> tagsList = tagAbstractRepository.findAllByArticleId(a.getId());
                List<Author> authorsList = authorRepository.findAllByArticleId(a.getId());
                List<ExpertReview> expertReviewsList = expertReviewRepository
                        .findAllByIdIsNotNullAnd_CommentPost_ArticleId(a.getId());

                if (tagsList != null && !tagsList.isEmpty()) {
                    List<String> tagNames = new ArrayList<>();
                    for (Tag tagObject : tagsList) {
                        tagNames.add(tagObject.getName());
                    }
                    a.setTags(tagNames);
                }
                List<CommenterDTO> authorCommenters = new ArrayList<>();
                for (Author authorObject : authorsList) {
                    String uname = authorObject.getUsername();
                    Optional<User> user = userRepository.findByUsername(uname);
                    String name = user.isPresent() ? user.get().getName() : null;
                    authorCommenters.add(new CommenterDTO(uname, name));
                }
                a.setAuthors(authorCommenters);
                if (expertReviewsList != null && !expertReviewsList.isEmpty()) {
                    List<String> expertReviews = new ArrayList<>();
                    for (ExpertReview expertReview : expertReviewsList) {
                        expertReviews.add(expertReview.getStatus());
                    }
                    a.setExpertReviews(expertReviews);
                }
                approvedArticles.add(a);
            }
        }

        return approvedArticles;

    }

    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Get(value = "/{username}/unapproved")
    public List<Article> viewUnapprovedArticlesForUser(Authentication authentication, @NotBlank String username) {
        if (!authentication.getName().equals(username)) {
            return Collections.emptyList();
        }
        
        List<Article> unapprovedArticles = new ArrayList<>();
        List<Author> authors = authorRepository.findByUsername(username);
        List<ExpertReview> allExpertReviews = expertReviewRepository.findAll();

        System.out.println("Authors: " + authors.toString());
        for (Author author : authors) {
            System.out.println("Author: " + author);
            Article a = author.getArticle();
            System.out.println("Article a approved: " + a.getApproved());
            if (a.getApproved() == 0) {
                List<Tag> tagsList = tagAbstractRepository.findAllByArticleId(a.getId());
                List<Author> authorsList = authorRepository.findAllByArticleId(a.getId());
                List<Link> linksList = linkRepository.findAllByArticleId(a.getId());
                List<String> reviewStatuses = new LinkedList<>();
                for (ExpertReview er : allExpertReviews) {
                    if (er.getCommentPost().getArticle().getId().equals(a.getId())) {
                        reviewStatuses.add(er.getStatus());
                    }
                }

                if (tagsList != null && !tagsList.isEmpty()) {
                    List<String> tagNames = new ArrayList<>();
                    for (Tag tagObject : tagsList) {
                        tagNames.add(tagObject.getName());
                    }
                    a.setTags(tagNames);
                }
                List<CommenterDTO> authorCommenters = new ArrayList<>();
                for (Author authorObject : authorsList) {
                    String uname = authorObject.getUsername();
                    Optional<User> user = userRepository.findByUsername(uname);
                    String name = user.isPresent() ? user.get().getName() : null;
                    authorCommenters.add(new CommenterDTO(uname, name));
                }
                a.setAuthors(authorCommenters);
                if (linksList != null && !linksList.isEmpty()) {
                    List<String> links = new ArrayList<>();
                    for (Link link : linksList) {
                        links.add(link.getUrl());
                    }
                    a.setLinks(links);
                }
                a.setExpertReviews(reviewStatuses);
                unapprovedArticles.add(a);
            }
        }

        return unapprovedArticles;

    }

    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Post(value = "/{articleId}/vote", consumes = { "application/json" })
    public HttpResponse<?> voteArticle(@NotBlank Long articleId, @Body String which) {
        Optional<Article> article = articleRepository.findById(articleId);
        if (article.isEmpty()) {
            return HttpResponse.status(HttpStatus.NOT_FOUND)
                    .body("Status " + HttpStatus.NOT_FOUND + ": Article does not exist.");
        } else if (!which.equals("up") && !which.equals("down")) {
            return HttpResponse.status(HttpStatus.BAD_REQUEST)
                    .body("Status " + HttpStatus.BAD_REQUEST + ": Vote must be 'up' or 'down'.");
        }

        int score = (which.equals("up")) ? article.get().getScore() + 1 : article.get().getScore() - 1;
        articleRepository.update(articleId, score);
        return HttpResponse.status(HttpStatus.OK).body(score);
    }

    @Get(value = "/top")
    public List<Article> getTopArticles() {
        List<Article> orderedArticles = articleRepository.listOrderByScore();
        Collections.reverse(orderedArticles);
        return orderedArticles.subList(0, Math.min(orderedArticles.size(), 3));
    }

}
