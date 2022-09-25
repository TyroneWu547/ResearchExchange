package research_exchange.services;

import io.micronaut.data.exceptions.DataAccessException;
import jakarta.inject.Singleton;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import research_exchange.forms.ExpertReviewForm;
import research_exchange.forms.HighlightSectionForm;
import research_exchange.forms.InlineCommentForm;
import research_exchange.models.Article;
import research_exchange.models.CommentPost;
import research_exchange.models.ExpertReview;
import research_exchange.models.GeneralComment;
import research_exchange.models.HighlightSection;
import research_exchange.models.InlineComment;
import research_exchange.models.User;
import research_exchange.repositories.ArticleRepository;
import research_exchange.repositories.AuthorRepository;
import research_exchange.repositories.ExpertRepository;
import research_exchange.repositories.ExpertReviewRepository;
import research_exchange.repositories.GeneralCommentRepository;
import research_exchange.repositories.InlineCommentRepository;
import research_exchange.repositories.UserRepository;
import research_exchange.dto.ExpertReviewDTO;
import research_exchange.dto.FollowupCommentDTO;
import research_exchange.dto.InlineCommentDTO;
import research_exchange.dto.UserReviewDTO;
import research_exchange.dto.CommenterDTO;

/**
 * The Expert Review Service class which handles the error checking before
 * inserting into database.
 *
 * @author Tyrone Wu
 */
@Singleton
public class ExpertReviewService {

    /** For accessing User DB table. */
    private final UserRepository userRepository;

    /** For accessing Author DB table. */
    private final AuthorRepository authorRepository;

    /** For accessing Expert DB table. */
    private final ExpertRepository expertRepository;

    /** For accessing Article DB table. */
    private final ArticleRepository articleRepository;

    /** For accessing Expert Review DB table. */
    private final ExpertReviewRepository expertReviewRepository;

    /** For accessing Inline Comment DB table. */
    private final InlineCommentRepository inlineCommentRepository;

    /** For accessing General Comment DB table. */
    private final GeneralCommentRepository generalCommentRepository;

    /**
     * Setup Expert Review, Article, and User repository into the Expert Review
     * service.
     * 
     * @param userRepository           the User Repository
     * @param articleRepository        the Article Repository
     * @param expertReviewRepository   the Expert Review repository
     * @param inlineCommentRepository  the Inline Comment repository
     * @param generalCommentRepository the General Comment repository
     */
    public ExpertReviewService(UserRepository userRepository, AuthorRepository authorRepository,
            ExpertRepository expertRepository, ArticleRepository articleRepository,
            ExpertReviewRepository expertReviewRepository, InlineCommentRepository inlineCommentRepository,
            GeneralCommentRepository generalCommentRepository) {
        this.userRepository = userRepository;
        this.authorRepository = authorRepository;
        this.expertRepository = expertRepository;
        this.articleRepository = articleRepository;
        this.expertReviewRepository = expertReviewRepository;
        this.inlineCommentRepository = inlineCommentRepository;
        this.generalCommentRepository = generalCommentRepository;
    }

    /**
     * Get all the Expert Reviews associated with the Article ID. A
     * NotFoundException is thrown if the Article post does not exist.
     * 
     * @param articleId the Article ID used to retrieve the Expert Reviews
     * @return list of Expert Reviews associated with the Article ID
     * @throws NotFoundException if the Article does not exist in the database
     */
    public List<ExpertReviewDTO> getArticleExpertReviewInfo(Long articleId) {
        // Check if Article exists
        Optional<Article> articleOption = articleRepository.findById(articleId);
        if (articleOption.isEmpty()) {
            throw new NotFoundException("Article does not exist.");
        }

        List<ExpertReview> allExpertReviews = expertReviewRepository.findAll();
        List<ExpertReview> articleExpertReviews = new LinkedList<>();
        for (ExpertReview er : allExpertReviews) {
            if (er.getCommentPost().getArticle().getId().equals(articleId)) {
                articleExpertReviews.add(er);
            }
        }

        List<String> articleAuthors = authorRepository.findAllByArticleId(articleId).stream().map(a -> a.getUsername())
                .collect(Collectors.toList());
        ;
        boolean approved = articleOption.get().getApproved() == 1;
        List<ExpertReviewDTO> expertReviewDTOs = formatExpertReviewList(articleExpertReviews, false);
        for (ExpertReviewDTO review : expertReviewDTOs) {
            modifyExpertReviewDTO(approved, articleAuthors, review);
        }
        return expertReviewDTOs;
    }

    /**
     * Get all the Expert Reviews associated with the User ID. A NotFoundException
     * is thrown if the User does not exist.
     * 
     * @param username the User's username used to retrieve the Expert Reviews
     * @return list of Expert Reviews associated with the User ID
     * @throws NotFoundException if the User does not exist in the database
     */
    public List<UserReviewDTO> getUserAllExpertReviews(String username) {
        // Check if User exists
        Optional<Long> userId = userRepository.findIdByUsername(username);
        if (userId.isEmpty()) {
            throw new NotFoundException("User does not exist.");
        }

        List<ExpertReview> allExpertReviews = expertReviewRepository.findAll();
        List<ExpertReview> userExpertReviews = new LinkedList<>();
        for (ExpertReview er : allExpertReviews) {
            if (er.getCommentPost().getUser().getId().equals(userId.get())) {
                userExpertReviews.add(er);
            }
        }
        List<UserReviewDTO> expertReviewViews = formatExpertReviewList(userExpertReviews, true).stream()
                .map(element -> (UserReviewDTO) element)
                .collect(Collectors.toList());
        return expertReviewViews;
    }

    /**
     * Format a list of expert reviews.
     *
     * @param expertReviews list of expert reviews to format
     * @param userView      if the view is on the user profile
     * @return array of formatted expert reviews
     */
    private List<ExpertReviewDTO> formatExpertReviewList(List<ExpertReview> expertReviews, boolean userView) {
        List<ExpertReviewDTO> expertReviewViews = new LinkedList<>();
        for (ExpertReview er : expertReviews) {
            List<InlineComment> inlineComments = inlineCommentRepository.findAllByExpertReviewId(er.getId());
            expertReviewViews.add(formatExpertReview(er, new CommenterDTO(er.getCommentPost().getUser()),
                    inlineComments, false, userView));
        }
        return expertReviewViews;
    }

    /**
     * Get a specific Expert Review along with its Inline Comments, Highlight
     * Sections, and Inline Followups.
     *
     * @param expertReviewId the Expert Review ID to get
     * @return Expert Review along with its Inline Comments, Highlight Sections, and
     *         Inline Followups
     */
    public ExpertReviewDTO getExpertReviewInfo(Long expertReviewId) {
        // Check if Expert Review exists in the Article
        Optional<ExpertReview> expertReview = expertReviewRepository.findByCommentPostId(expertReviewId);
        if (expertReview.isEmpty()) {
            throw new NotFoundException("Expert Review does not exist.");
        }

        Article article = expertReview.get().getCommentPost().getArticle();
        List<String> articleAuthors = authorRepository.findAllByArticleId(article.getId()).stream()
                .map(a -> a.getUsername()).collect(Collectors.toList());
        boolean approved = article.getApproved() == 1;

        // Get Expert Review
        List<InlineComment> inlineComments = inlineCommentRepository
                .findAllByExpertReviewId(expertReview.get().getId());
        ExpertReviewDTO review = formatExpertReview(expertReview.get(),
                new CommenterDTO(expertReview.get().getCommentPost().getUser()), inlineComments, false, false);
        modifyExpertReviewDTO(approved, articleAuthors, review);
        return review;
    }

    /**
     * Edits an expert review's status with the new status.
     *
     * @param expertReviewId the expert review's ID
     * @param newStatus      the new status to update with
     * @throws NotFoundException        if the expert review does not exist
     * @throws IllegalArgumentException if the article of the expert review is
     *                                  already approved
     */
    public ExpertReviewDTO editExpertReviewStatus(String authorName, Long expertReviewId, String newStatus) {
        // Check if expert review exists
        Optional<ExpertReview> expertReview = expertReviewRepository.findByCommentPostId(expertReviewId);
        if (expertReview.isEmpty()) {
            throw new NotFoundException("Expert Review does not exist.");
        } else if (expertReview.get().getCommentPost().getArticle().getApproved() == 1) {
            throw new IllegalArgumentException("Cannot edit status on an already approved Article.");
        } else if (!expertReview.get().getCommentPost().getUser().getUsername().equals(authorName)) {
            throw new NotAuthorizedException("Can only edit status of article you have posted");
        }

        // Update the expert review's status
        expertReviewRepository.updateByCommentPostId(expertReviewId, newStatus);

        // Get Expert Review
        List<InlineComment> inlineComments = inlineCommentRepository
                .findAllByExpertReviewId(expertReview.get().getId());
        ExpertReviewDTO view = formatExpertReview(expertReview.get(),
                new CommenterDTO(expertReview.get().getCommentPost().getUser()), inlineComments, false, false);
        return view;
    }

    /**
     * Post Expert Review to an Article post. A NotFoundException is thrown if the
     * User and Article. An IllegalArgumentException is thrown if the Expert Review
     * is empty or status is not a valid value.
     *
     * @param articleId        the Article ID of where to post the Expert Review
     * @param expertReviewForm the Expert Review to post
     * @return the UUID of the posted Expert Review
     * @throws NotFoundException        if the User and Article does not exist in
     *                                  the database
     * @throws IllegalArgumentException if the review is empty or if status is not
     *                                  valid
     */
    public ExpertReviewDTO postExpertReview(Long articleId, ExpertReviewForm expertReviewForm) {
        // Check if User exists
        Optional<Long> userId = userRepository.findIdByUsername(expertReviewForm.getAuthor());
        if (userId.isEmpty()) {
            throw new NotFoundException("User does not exist.");
        }
        // Check if Article exists
        Optional<Integer> approved = articleRepository.findApprovedById(articleId);
        if (approved.isEmpty()) {
            throw new NotFoundException("Article does not exist.");
        }
        // Check if Article is already `Approved`
        else if (approved.get().equals(1)) {
            throw new IllegalArgumentException("Article has already been approved.");
        }

        // Check if User already posted Expert Review on the Article
        for (ExpertReview er : expertReviewRepository.findAll()) {
            if (er.getCommentPost().getUser().getId().equals(userId.get())
                    && er.getCommentPost().getArticle().getId().equals(articleId)) {
                throw new IllegalArgumentException("User has already posted an Expert Review on this Article.");
            }
        }

        // Check if content of Expert Review is empty
        if (expertReviewForm.getContent() == null || expertReviewForm.getContent().isEmpty()) {
            throw new IllegalArgumentException("Review is empty.");
        }
        // Check if status of Review is valid
        else if (!expertReviewForm.getStatus().equals("Approved") && !expertReviewForm.getStatus().equals("Needs Work")
                && !expertReviewForm.getStatus().equals("Rejected")) {
            throw new IllegalArgumentException("Status must be 'Approved', 'Needs Work', or 'Rejected'.");
        }

        ExpertReview expertReview = null;
        try {
            // Save Expert Review
            CommentPost commentPost = new CommentPost(new User(userId.get()), new Article(articleId), 0,
                    expertReviewForm.getContent());
            expertReview = new ExpertReview(commentPost, expertReviewForm.getStatus());
            expertReview = expertReviewRepository.save(expertReview);

            // Check if this is the third approved review for specific article
            List<ExpertReview> expertReviews = expertReviewRepository.findAll();
            Article article = articleRepository.findById(articleId).get();
            int approvedCount = 0;
            for (ExpertReview er : expertReviews) {
                if (er.getCommentPost().getArticle().getId().equals(articleId)) {
                    if (er.getStatus().equals("Approved")) {
                        approvedCount++;
                    }
                    if (approvedCount >= 3) {
                        if (article.getApproved() == 0) {
                            article.setApproved(1);
                        }
                        articleRepository.update(article);
                    }
                }
            }

            // Save inline comments
            List<InlineComment> inlineComments = null;
            if (expertReviewForm.getInlineComments() != null && !expertReviewForm.getInlineComments().isEmpty()) {
                inlineComments = new ArrayList<>(expertReviewForm.getInlineComments().size());
                for (InlineCommentForm icf : expertReviewForm.getInlineComments()) {
                    commentPost = new CommentPost(new User(userId.get()), new Article(articleId), 0, icf.getContent());
                    InlineComment ic = new InlineComment(commentPost, expertReview, icf.getPageNum(),
                            icf.getSelectedContent());

                    // Attach highlight sections to the inline comment
                    List<HighlightSection> highlightSections = new ArrayList<>(icf.getHighlightSections().size());
                    for (HighlightSectionForm hsf : icf.getHighlightSections()) {
                        highlightSections.add(new HighlightSection(ic, hsf));
                    }
                    ic.setHighlightSections(highlightSections);

                    inlineComments.add(ic);

                }
                inlineCommentRepository.saveAll(inlineComments);
            }

            return formatExpertReview(expertReview, new CommenterDTO(expertReviewForm.getAuthor()), inlineComments,
                    true, false);
        } catch (DataAccessException e) {
            if (expertReview != null && expertReview.getId() != null) {
                expertReviewRepository.deleteById(expertReview.getId());
            }
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private void modifyCommentAuthor(boolean approved, List<String> articleAuthors, CommenterDTO author) {
        if (articleAuthors.contains(author.getUsername())) {
            if (!approved) {
                author.setUsername("Anonymous");
                author.setName("Anonymous");
            }
            author.setRole("Author");
        } else if (author.getRole().equals("Expert")) {
            String expertField = expertRepository.findByUsername(author.getUsername()).get().getField();
            author.setRole("Expert in " + expertField);
        }
    }

    private void modifyExpertReviewDTO(boolean approved, List<String> articleAuthors, ExpertReviewDTO review) {
        modifyCommentAuthor(approved, articleAuthors, review.getAuthor());

        for (FollowupCommentDTO followupDTO : review.getFollowups()) {
            modifyCommentAuthor(approved, articleAuthors, followupDTO.getAuthor());
        }

        for (InlineCommentDTO inlineDTO : review.getInlineComments()) {
            modifyCommentAuthor(approved, articleAuthors, inlineDTO.getAuthor());

            for (FollowupCommentDTO inlineFollowupDTO : inlineDTO.getFollowups()) {
                modifyCommentAuthor(approved, articleAuthors, inlineFollowupDTO.getAuthor());
            }
        }
    }

    /**
     * Format an ExpertReview to follow to view.
     *
     * @param expertReview   the expert review to format
     * @param author         the author of the expert review
     * @param inlineComments the inline comments of the expert review
     * @param fresh          if the expert review is fresh
     * @param userView       if the view is for the user
     * @return a formatted Expert Review
     */
    private ExpertReviewDTO formatExpertReview(ExpertReview expertReview, CommenterDTO author,
            List<InlineComment> inlineComments,
            boolean fresh, boolean userView) {
        // Attach followups to expert review
        List<GeneralComment> erFollowups = null;
        if (!fresh) {
            erFollowups = generalCommentRepository.findAllByRootThreadId(expertReview.getCommentPost().getId());
        }

        // Attach inline comments to expert review
        List<InlineCommentDTO> icViews = new LinkedList<>();
        if (inlineComments != null) {
            for (InlineComment ic : inlineComments) {
                // Attach followups to inline comment
                List<GeneralComment> icFollowups = null;
                if (!fresh) {
                    icFollowups = generalCommentRepository.findAllByRootThreadId(ic.getCommentPost().getId());
                }
                icViews.add(new InlineCommentDTO(ic, author, icFollowups));
            }
        }

        // Format if on user profile
        if (userView) {
            return new UserReviewDTO(expertReview, author, erFollowups, icViews);
        } else {
            return new ExpertReviewDTO(expertReview, author, erFollowups, icViews);
        }
    }

}
