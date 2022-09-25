package research_exchange;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.List;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.transaction.Transactional;

import org.apache.commons.codec.binary.Hex;

import io.micronaut.context.annotation.Requires;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Singleton;
import research_exchange.dto.AbstractCommentDTO;
import research_exchange.dto.ExpertReviewDTO;
import research_exchange.forms.CommentForm;
import research_exchange.forms.ExpertReviewForm;
import research_exchange.forms.HighlightSectionForm;
import research_exchange.forms.InlineCommentForm;
import research_exchange.models.Article;
import research_exchange.models.Author;
import research_exchange.models.Expert;
import research_exchange.models.Link;
import research_exchange.models.Tag;
import research_exchange.models.User;
import research_exchange.repositories.ArticleRepository;
import research_exchange.repositories.AuthorRepository;
import research_exchange.repositories.CommentPostRepository;
import research_exchange.repositories.ExpertRepository;
import research_exchange.repositories.ExpertRequestRepository;
import research_exchange.repositories.ExpertReviewRepository;
import research_exchange.repositories.GeneralCommentRepository;
import research_exchange.repositories.HighlightSectionRepository;
import research_exchange.repositories.InlineCommentRepository;
import research_exchange.repositories.LinkRepository;
import research_exchange.repositories.TagAbstractRepository;
import research_exchange.repositories.UserRepository;
import research_exchange.services.CommentService;
import research_exchange.services.ExpertReviewService;

@Singleton
@Requires(notEnv = "test")
public class DataPopulator {

    private final UserRepository userRepository;

    private final ExpertRepository expertRepository;

    private final ArticleRepository articleRepository;

    private final AuthorRepository authorRepository;

    private final TagAbstractRepository tagRepository;

    private final LinkRepository linkRepository;

    private final ExpertReviewRepository expertReviewRepository;

    private final ExpertReviewService expertReviewService;

    private final CommentPostRepository commentPostRepository;

    private final GeneralCommentRepository generalCommentRepository;

    private final InlineCommentRepository inlineCommentRepository;

    private final CommentService commentService;

    private final HighlightSectionRepository highlightSectionRepository;

    private final ExpertRequestRepository expertRequestRepository;

    public DataPopulator(UserRepository userRepository, ExpertRepository expertRepository,
            ArticleRepository articleRepository, AuthorRepository authorRepository,
            TagAbstractRepository tagRepository,
            LinkRepository linkRepository, ExpertReviewRepository expertReviewRepository,
            ExpertReviewService expertReviewService, CommentPostRepository commentPostRepository,
            GeneralCommentRepository generalCommentRepository,
            InlineCommentRepository inlineCommentRepository,
            CommentService commentService,
            HighlightSectionRepository highlightSectionRepository,
            ExpertRequestRepository expertRequestRepository) {
        this.userRepository = userRepository;
        this.expertRepository = expertRepository;
        this.articleRepository = articleRepository;
        this.authorRepository = authorRepository;
        this.tagRepository = tagRepository;
        this.linkRepository = linkRepository;
        this.expertReviewRepository = expertReviewRepository;
        this.expertReviewService = expertReviewService;
        this.commentPostRepository = commentPostRepository;
        this.generalCommentRepository = generalCommentRepository;
        this.inlineCommentRepository = inlineCommentRepository;
        this.commentService = commentService;
        this.highlightSectionRepository = highlightSectionRepository;
        this.expertRequestRepository = expertRequestRepository;
    }

    @EventListener
    @Transactional
    void init(StartupEvent event) {
        // Clear everything
        userRepository.deleteAll();
        expertRepository.deleteAll();
        articleRepository.deleteAll();
        authorRepository.deleteAll();
        tagRepository.deleteAll();
        linkRepository.deleteAll();
        expertReviewRepository.deleteAll();
        commentPostRepository.deleteAll();
        generalCommentRepository.deleteAll();
        inlineCommentRepository.deleteAll();
        highlightSectionRepository.deleteAll();
        expertRequestRepository.deleteAll();

        // Generate the same password for everyone
        byte[] salt = "salt".getBytes();
        String hashedPassword = hashPassword("password", salt);

        // Add general users
        User admin = new User("Admin", "admin", "admin@gmail.com", hashedPassword, salt, "Admin");
        User user1 = new User("Username", "username", "username@gmail.com", hashedPassword, salt, "User");
        User userExpert2 = new User("Expert", "expert", "expert@gmail.com", hashedPassword, salt, "Expert");
        userRepository.saveAll(Arrays.asList(admin, user1, userExpert2));

        Expert expert2 = new Expert("expert", "Computer Science");
        expertRepository.save(expert2);

        // Add article 1
        Article a1 = new Article("Cytoscape: the network visualization tool for GenomeSpace workflows",
                "Modern genomic analysis often requires workflows incorporating multiple best-of-breed tools. GenomeSpace is a web-based visual workbench that combines a selection of these tools with mechanisms that create data flows between them. One such tool is Cytoscape 3, a popular application that enables analysis and visualization of graph-oriented genomic networks. As Cytoscape runs on the desktop, and not in a web browser, integrating it into GenomeSpace required special care in creating a seamless user experience and enabling appropriate data flows. In this paper, we present the design and operation of the Cytoscape GenomeSpace app, which accomplishes this integration, thereby providing critical analysis and visualization functionality for GenomeSpace users. It has been downloaded over 850 times since the release of its first version in September, 2013.",
                "Biology", "Genetics",
                "https://objectstorage.us-ashburn-1.oraclecloud.com/n/idvpzhveofap/b/research-exchange-pdf-storage-bucket/o/Biology_Genetics_Networks_Trimmed.pdf",
                "https://github.com/idekerlab/genomespace-cytoscape-weblaunch", null);
        a1.setScore(103);
        Article savedA1 = articleRepository.save(a1);

        User a1AuthorUser1 = new User("Barry Demchak", "bdemchak", "bdemchak@gmail.com", hashedPassword, salt,
                "User");
        User a1AuthorUser2 = new User("Tim Hull", "thull", "thull@gmail.com", hashedPassword, salt, "User");
        User a1AuthorUser3 = new User("Michael Reich", "mreich", "mreich@gmail.com", hashedPassword, salt,
                "User");
        User a1AuthorUser4 = new User("Ted Liefeld", "tliefeld", "tliefeld@gmail.com", hashedPassword, salt,
                "User");
        User a1AuthorUser5 = new User("Michael Smoot", "msmoot", "msmoot@gmail.com", hashedPassword, salt,
                "User");
        User a1AuthorUser6 = new User("Trey Ideker", "tideker", "tideker@gmail.com", hashedPassword, salt,
                "User");
        User a1AuthorUser7 = new User("Jill P. Mesirov", "jpmesirov", "jpmesirov@gmail.com", hashedPassword,
                salt,
                "User");

        userRepository.saveAll(Arrays.asList(a1AuthorUser1, a1AuthorUser2, a1AuthorUser3, a1AuthorUser4,
                a1AuthorUser5, a1AuthorUser6, a1AuthorUser7));

        Author a1Author1 = new Author("bdemchak", savedA1);
        Author a1Author2 = new Author("thull", savedA1);
        Author a1Author3 = new Author("mreich", savedA1);
        Author a1Author4 = new Author("tliefeld", savedA1);
        Author a1Author5 = new Author("msmoot", savedA1);
        Author a1Author6 = new Author("tideker", savedA1);
        Author a1Author7 = new Author("jpmesirov", savedA1);
        authorRepository
                .saveAll(Arrays.asList(a1Author1, a1Author2, a1Author3, a1Author4, a1Author5, a1Author6,
                        a1Author7));

        Tag a1Tag1 = new Tag("Software Tool", savedA1);
        Tag a1Tag2 = new Tag("Networks", savedA1);
        Tag a1Tag3 = new Tag("Graphs", savedA1);
        tagRepository.saveAll(Arrays.asList(a1Tag1, a1Tag2, a1Tag3));

        Link a1Link1 = new Link("http://genomespace.org/", savedA1);
        Link a1Link2 = new Link("https://apps.cytoscape.org/apps/genomespace", savedA1);
        Link a1Link3 = new Link("https://f1000research.com/articles/3-151/v2", savedA1);
        linkRepository.saveAll(Arrays.asList(a1Link1, a1Link2, a1Link3));

        User a1UserExpert1 = new User("Aris Floratos", "aflorat", "aflorat@gmail.com", hashedPassword, salt,
                "Expert");
        User a1UserExpert2 = new User("Gabriela Bindea", "gbindea", "gbindea@gmail.com", hashedPassword, salt,
                "Expert");
        User a1UserExpert3 = new User("Jan Aerts", "jaerts", "jaerts@gmail.com", hashedPassword, salt,
                "Expert");
        userRepository.saveAll(Arrays.asList(a1UserExpert1, a1UserExpert2, a1UserExpert3));

        Expert a1Expert1 = new Expert("aflorat", "Biology");
        Expert a1Expert2 = new Expert("gbindea", "Biology");
        Expert a1Expert3 = new Expert("jaerts", "Computer Science");
        expertRepository.saveAll(Arrays.asList(a1Expert1, a1Expert2, a1Expert3));

        InlineCommentForm a1e1i1 = new InlineCommentForm(2, "Demonstration",
                "The demonstration would benefit from a more detailed analysis workflow. E.g., a gene expression data set and a class file can be processed by GenePattern (or some other GenomeSpace tool) to generate a list of differentially expressed genes for some reasonable pair of case/control phenotypes and then feed the resulting file into Cytoscape. This could also help demonstrate the value proposition of GenomeSpace, i.e., providing the ability to seamlessly use multiple tools within an integrated environment. The authors may also want to briefly discuss why people are often interested in visualizing enriched/differentially-expressed genes in the context of a network, this would help the less-initiated better appreciate the value of having Cytoscape available as a GenomeSpace tool.",
                Arrays.asList(new HighlightSectionForm(513, 976, 97, 17)));
        InlineCommentForm a1e1i2 = new InlineCommentForm(2,
                "It also initializes basic CDK-related state(e.g., the GenomeSpaceContext root context).",
                "GenomeSpace-specific objects are used without prior definition/explanation.",
                Arrays.asList(new HighlightSectionForm(248, 902, 241, 16),
                        new HighlightSectionForm(92, 920, 268, 16)));
        InlineCommentForm a1e1i3 = new InlineCommentForm(2,
                "As withother apps, the app's cyActivator initializes the app state, includinggaining references to the standard Cy objects: application, network,view, and table managers.",
                "Terms like \"cyActivator\" and \"Cy objects\" are used without prior definition/explanation.",
                Arrays.asList(new HighlightSectionForm(437, 848, 47, 16),
                        new HighlightSectionForm(92, 866, 393, 14),
                        new HighlightSectionForm(92, 866, 393, 16),
                        new HighlightSectionForm(92, 866, 393, 16),
                        new HighlightSectionForm(92, 884, 393, 14),
                        new HighlightSectionForm(92, 884, 393, 16),
                        new HighlightSectionForm(92, 902, 151, 16)));
        InlineCommentForm a1e1i4 = new InlineCommentForm(2, "GenomeSpace communication strategy",
                "The article refers to Cytoscape-specific programming constructs without introducing them or providing relevant references for disambiguation.",
                Arrays.asList(new HighlightSectionForm(92, 739, 262, 17)));
        InlineCommentForm a1e1i5 = new InlineCommentForm(1,
                "GenomeSpace con-structs the JNLP URL to contain a GenomeSpace file descriptoras a parameter, and the PHP script extracts it and defines it as aparameter to the LaunchHelper (see Supplementary Data, particu-larly as the value used for the SomeGenomeSpaceFileID in thegs.url argument).",
                "It would add clarity to explain what the file referenced is.",
                Arrays.asList(new HighlightSectionForm(791, 698, 115, 16),
                        new HighlightSectionForm(513, 716, 393, 14),
                        new HighlightSectionForm(513, 716, 393, 16),
                        new HighlightSectionForm(513, 734, 393, 14),
                        new HighlightSectionForm(513, 734, 393, 16),
                        new HighlightSectionForm(513, 752, 393, 14),
                        new HighlightSectionForm(513, 752, 393, 16),
                        new HighlightSectionForm(513, 770, 393, 14),
                        new HighlightSectionForm(513, 770, 393, 16),
                        new HighlightSectionForm(513, 788, 101, 16)));
        InlineCommentForm a1e1i6 = new InlineCommentForm(1,
                "Paradoxically, Java-based tools (such asCytoscape) run directly on the user's workstation instead of withina browser.",
                "It is not clear what is paradoxical about the described execution protocol. The word \"paradoxically\" should/could be dropped or maybe be replaced by a more appropriate adverb.",
                Arrays.asList(new HighlightSectionForm(243, 1100, 238, 16),
                        new HighlightSectionForm(92, 1118, 392, 14),
                        new HighlightSectionForm(92, 1118, 392, 16),
                        new HighlightSectionForm(92, 1136, 61, 16)));
        InlineCommentForm a1e1i7 = new InlineCommentForm(1, "GenomeSpace app",
                "This section appears written with the assumption that the reader is already familiar with the Cytoscape app framework, essentially no background is provided.",
                Arrays.asList(new HighlightSectionForm(513, 1081, 129, 17)));
        InlineCommentForm a1e1i8 = new InlineCommentForm(3, "enrichment data",
                "It would be helpful to describe what the columns of the input data file EnrichmentData.dat represent.",
                Arrays.asList(new HighlightSectionForm(92, 372, 92, 16)));
        List<InlineCommentForm> a1e1i = Arrays.asList(a1e1i1, a1e1i2, a1e1i3, a1e1i4, a1e1i5, a1e1i6, a1e1i7,
                a1e1i8);

        ExpertReviewDTO a1r1 = expertReviewService.postExpertReview(savedA1.getId(), new ExpertReviewForm(
                a1UserExpert1.getUsername(),
                "Language/spelling: The language is acceptable but would benefit from a more careful reading/editing.\n\nOverall Impression: This is an informative article, describing how a specific tool (Cytoscape) has been integrated into an innovative framework (GenomeSpace) that enables seamless access to multiple bioinformatics tools. The main concern is overall readability in the absence of sufficient background on the Cytoscape app framework, to put the implementation of the GenomeSpace app in proper context. The other issues are minor and can be easily addressed.",
                "Approved", a1e1i));

        commentService.postComment(a1.getId(), new CommentForm(a1r1.getId(), a1r1.getId(),
                a1AuthorUser1.getUsername(),
                "Thank you, Aris. Your observations and suggestions are very valuable, and we'll upgrade the paper accordingly. We really appreciate your detailed observations. Look for a new version in a couple of days."));

        ExpertReviewDTO a1r2 = expertReviewService.postExpertReview(savedA1.getId(), new ExpertReviewForm(
                a1UserExpert2.getUsername(),
                "The paper \"Cytoscape: the network visualization tool for GenomeSpace workflows\" describes the GenomeSpace app, a tool that facilitates data analysis in Cytoscape for GenomeSpace users. This is a nice try to bridge two communities: cytoscapers & genomespacers.\n\nThe resources and tools included in GenomeSpace are well presented, and the important data sharing possibility is underlined. At the same time, the extensive app collection of Cytoscape with its multiple analysis possibilities is very short mentioned. This could be maybe extended so that users of GenomeSpace could use Cytoscape not only to visualize their results obtained in GenomeSpace, but also to analyze their data with Cytoscape apps. But this will be probably the topic of another paper.\n\nFor running the app, the computer should have enough memory, as mentioned in the article. Some GUI adjustments should be done for the different OS, for instance in Ubuntu the login dialog size should be increased, the buttons are not entirely visible at the moment. The new functionality is nicely underlined with icons, but is scattered throughout the menu. To increase the usability/visibility, after the login it would be good e.g. to add also the import/export buttons into the tool bar in top.\n\nThe app performs well. It remains to the users of both communities to discover the multiple possibilities that this app offers.",
                "Approved", null));

        commentService.postComment(a1.getId(), new CommentForm(a1r2.getId(), a1r2.getId(),
                a1AuthorUser1.getUsername(),
                "Thanks, Gabriela, for all of your helpful comments. The timing is good, as we're working a new Cytoscape release. We'll take a closer look at the dialog boxes. I don't think there's anything we can do about toolbar icons until the Cytoscape 3.3 release in 2015, but we'll certainly try to add the feature then."));

        InlineCommentForm a1e3i1 = new InlineCommentForm(2, "cyActivator",
                "This term cannot be expected to be understood without further explanation.",
                Arrays.asList(new HighlightSectionForm(216, 866, 69, 16)));
        InlineCommentForm a1e3i2 = new InlineCommentForm(1, "JRAC",
                "This term cannot be expected to be understood without further explanation.",
                Arrays.asList(new HighlightSectionForm(513, 1024, 35, 16)));
        List<InlineCommentForm> a1e3i = Arrays.asList(a1e3i1, a1e3i2);

        ExpertReviewDTO a1r3 = expertReviewService.postExpertReview(savedA1.getId(), new ExpertReviewForm(
                a1UserExpert3.getUsername(),
                "In the paper \"Cytoscape: the network visualization tool for GenomeSpace workflows\", Demchak et al. describe the design, implementation and operation of an interaction layer between Cytoscape and GenomeSpace. Unfortunately, I was not able to run the example described in the manuscript, due to a \"unable to find the document\" error. Overall, the manuscript gives a fair description both of the technology used for integrating Cytoscape into GenomeSpace and its use. This however does mean that the actual focus is not completely clear. If the message is \"there is a new tool accessible from GenomeSpace\" which would be aimed at the end-user, then terms like \"JRAC\" and \"cyActivator\" cannot be expected to be understood without further explanation. Care should be given to explain these sufficiently enough for lay-people. On the other hand, the message could also be \"here is how to integrate a web-tool with a desktop application\", in which case the technical part might be described a bit more in depth. I expect this paper and the bridge between Cytoscape and GenomeSpace will be of considerable interest to the community.",
                "Approved", a1e3i));

        commentService.postComment(a1.getId(), new CommentForm(a1r3.getId(), a1r3.getId(),
                a1AuthorUser1.getUsername(),
                "Thank you, Jan, for the thoughtful comments. We are looking into the issue of Cytoscape not launching from the web site. As a workaround, you can either hit the OK button on the browser error window, or launch Cytoscape on your PC before attempting to launch it from GenomeSpace. You can also get the Cytoscape session file directly from GenomeSpace by launching Cytoscape, and the next few days. Thank you!"));

        User a1user1 = new User("Nigam Shah", "nshah", "nshah@gmail.com", hashedPassword, salt, "User");
        User a1user2 = new User("Michel Dumontier", "mdumont", "mdumont@gmail.com", hashedPassword, salt,
                "User");
        userRepository.saveAll(Arrays.asList(a1user1, a1user2));

        commentService.postComment(a1.getId(), new CommentForm(null, null, a1user1.getUsername(),
                "Tying together the best Web based workflow software for genomic analysis with the best network analysis tool is highly valuable. Fun to give it a try!"));

        AbstractCommentDTO a1c2 = commentService.postComment(a1.getId(), new CommentForm(null, null,
                a1user2.getUsername(),
                "Linking Cytoscape with other applications through GenomeSpace seems like a small step to starting tying together different software. To demonstrate the value of using GenomeSpace, the authors should walk us through an example that makes use of other software to generate cytoscape input and/or further process cytoscape output. Otherwise, how is this different than just using Cytoscape? Additionally, the paper should clearly articulate the scientific merit of its example: what biological insight do we obtain in overlaying gene expression data on this pre-generated network file?"));

        commentService.postComment(a1.getId(), new CommentForm(a1c2.getId(), a1c2.getId(),
                a1AuthorUser1.getUsername(),
                "Thanks for the thoughtful comments. The ground rules for this paper collection involved writing a 2 pager (or slightly more) on a Cytoscape App. So, this paper is App-centric.Your point, though, goes towards demonstrating the value of GenomeSpace in the first place, which is yet a separate paper, and would be enormously valuable. We'll try to write that next."));

        // Add article 2
        Article a2 = new Article(
                "Survey to healthcare professionals on the practicality of AI services for healthcare",
                "Background: Artificial Intelligence (AI) is already in use in many fields of healthcare, and utilization of AI in state-of-the art healthcare services is growing, with many studies showing that AI can provide benefits in healthcare processes. However, there is a need for research about healthcare professionals' willingness to use AI-based services, and their consideration about in which cases AI could provide better healthcare outcomes and reduce healthcare professionals' workload.\n\nMethods: We studied the latest technologies and methods from current healthcare AI services' end-users to create a survey on healthcare services, including use cases that could be beneficial for healthcare professionals.  We focused on studying Information and Communication Technology (ICT) applications and services which utilize modern AI features. The purpose of our survey for healthcare professionals was to provide an analysis of the healthcare services that utilize AI features, which could provide better health outcomes from a healthcare professional and care process point of view.\n\nResults: AI features, such as health monitoring, medication management, and connected machines are considered to have high benefit in healthcare workflow. Moreover, we found that the majority (67.8%) of healthcare professionals are willing to use AI for supporting decision making or even providing independent diagnosis. In total, 28.9% of healthcare professionals are willing to use AI in a restricted manner to help professionals in care processes. Only 3.3% of respondents are not willing to use AI-based services at all.\n\nConclusions: Our survey indicated that the willingness of healthcare professionals to use different AI in healthcare technology solutions is high.",
                "Computer Science", "Artificial Intelligence",
                "https://objectstorage.us-ashburn-1.oraclecloud.com/n/idvpzhveofap/b/research-exchange-pdf-storage-bucket/o/CSC_AI_Healthcare_Trimmed.pdf",
                null, "https://osf.io/674zm");
        a2.setScore(0);
        Article savedA2 = articleRepository.save(a2);

        User a2AuthorUser1 = new User("Antti Väänänen", "avaananen", "avaananen@gmail.com", hashedPassword,
                salt,
                "User");
        User a2AuthorUser2 = new User("Keijo Haataja", "khaataja", "khaataja@gmail.com", hashedPassword, salt,
                "User");
        User a2AuthorUser3 = new User("Pekka Toivanen", "ptoivanen", "ptoivanen@gmail.com", hashedPassword,
                salt,
                "User");

        userRepository.saveAll(Arrays.asList(a2AuthorUser1, a2AuthorUser2, a2AuthorUser3));

        Author a2Author1 = new Author("avaananen", savedA2);
        Author a2Author2 = new Author("khaataja", savedA2);
        Author a2Author3 = new Author("ptoivanen", savedA2);
        authorRepository.saveAll(Arrays.asList(a2Author1, a2Author2, a2Author3));

        User a2UserExpert1 = new User("Parag Chatterjee", "pchatte", "pchatte@gmail.com", hashedPassword, salt,
                "Expert");
        User a2UserExpert2 = new User("Charlotte Blease", "cblease", "gbindea@gmail.com", hashedPassword, salt,
                "Expert");
        User a2UserExpert3 = new User("Lorenzo Faggioni", "lfaggio", "lfaggio@gmail.com", hashedPassword, salt,
                "Expert");
        userRepository.saveAll(Arrays.asList(a2UserExpert1, a2UserExpert2, a2UserExpert3));

        Expert a2Expert1 = new Expert("pchatte", "Computer Science");
        Expert a2Expert2 = new Expert("cblease", "Mathematics");
        Expert a2Expert3 = new Expert("lfaggio", "Computer Science");
        expertRepository.saveAll(Arrays.asList(a2Expert1, a2Expert2, a2Expert3));

        InlineCommentForm a2e1i1 = new InlineCommentForm(2, "ICT technology",
                "The use of the words 'ICT Technology' is redundant, since ICT itself is 'Information and Communication Technology'.",
                Arrays.asList(new HighlightSectionForm(613, 462, 100, 16)));
        List<InlineCommentForm> a2e1i = Arrays.asList(a2e1i1);

        expertReviewService.postExpertReview(savedA2.getId(), new ExpertReviewForm(a2UserExpert1.getUsername(),
                "The work is aimed at an interesting aspect of Artificial Intelligence in healthcare - the acceptability and opinions of the key stakeholders of the medical and healthcare system. The survey has been conducted in a well-structured manner, and the results are illustrated and analyzed well. However, considering the size of the cohort of this survey, a brief introduction and background of the participants would have been useful. Since some parts of the survey are quite subjective, it is important to explain the healthcare system and intrinsic relationships as well, where these insights from the survey hold true. Based on the survey outcomes, it is interesting to see a high level of acceptance in healthcare professionals towards Artificial Intelligence in healthcare.\n\nIs the work clearly and accurately presented and does it cite the current literature?\nYes\n\nIs the study design appropriate and is the work technically sound?\nYes\n\nAre sufficient details of methods and analysis provided to allow replication by others?\nYes\n\nIf applicable, is the statistical analysis and its interpretation appropriate?\nPartly\n\nAre all the source data underlying the results available to ensure full reproducibility?\nYes\n\nAre the conclusions drawn adequately supported by the results?\nPartly",
                "Needs Work", a2e1i));

        InlineCommentForm a2e2i1 = new InlineCommentForm(1, "Introduction",
                "There is no adequate literature review, nor a clear outline of the objectives in light of the background literature.",
                Arrays.asList(new HighlightSectionForm(92, 146, 90, 18)));
        List<InlineCommentForm> a2e2i = Arrays.asList(a2e2i1);

        expertReviewService.postExpertReview(savedA2.getId(), new ExpertReviewForm(a2UserExpert2.getUsername(),
                "Unfortunately, there are some serious problems with this survey. The abstract does not tell us anything about the sample or response rate. We later learn that the survey is drawn from a convenience sample drawn from a local network in Finland. There is therefore no assurance about the representativeness of health professionals in Finland. Indeed, the respondents include a variety of professions - physicians, nurses, admin staff, maintenance staff. Therefore we do not have a clear understanding of the attitudes/opinions about a particular demographic group of health professionals in Finland. The variety of perspectives limits the possibility of drawing robust inferences. We also need some commentary on whether it is is useful to survey health professionals on these questions. i.e. why would we expect health professionals to have a clear understanding of the applications of AI/ML to healthcare? This was not addressed. What do the investigators consider the utility of the results of the survey to be? Educational interventions? The authors do not adequately address the limitations of the survey.\n\nIs the work clearly and accurately presented and does it cite the current literature?\nPartly\n\nIs the study design appropriate and is the work technically sound?\nNo\n\nAre sufficient details of methods and analysis provided to allow replication by others?\nNo\n\nIf applicable, is the statistical analysis and its interpretation appropriate?\nI cannot comment. A qualified statistician is required.\n\nAre all the source data underlying the results available to ensure full reproducibility?\nPartly\n\nAre the conclusions drawn adequately supported by the results?\nNo",
                "Rejected", a2e2i));

        InlineCommentForm a2e3i1 = new InlineCommentForm(5, "Discussion",
                "This entire section should be focused on systematically comparing the study findings with those of the relevant existing literature. Basically, the only comparison was made with the YouGov survey, yet there are several surveys published in the literature regarding the awareness of healthcare professionals about AI and their expectations (either positive or negative) about its usability.",
                Arrays.asList(new HighlightSectionForm(513, 646, 82, 18)));
        InlineCommentForm a2e3i2 = new InlineCommentForm(2,
                "We received a total of 125 responses.Of these, only 121 responses were 100% complete and thereforeonly these responses were analyzed.",
                "I would mention the rather small study sample (N=121 complete responses) as a potential limitation of the study and comment on whether such limitation can impact the study findings and conclusions.",
                Arrays.asList(new HighlightSectionForm(248, 572, 237, 16),
                        new HighlightSectionForm(92, 590, 393, 14),
                        new HighlightSectionForm(92, 590, 393, 16),
                        new HighlightSectionForm(92, 608, 212, 16)));
        InlineCommentForm a2e3i3 = new InlineCommentForm(2, "Figure 1",
                "The survey findings and demographic data of the survey respondents were reported and illustrated in detail, but apparently without any sort of statistical analysis (which could allow drawing rigorous conclusions based on statistical significance). Hence, I would strongly encourage the authors to analyze their data using inferential statistical methods (including p-values, confidence intervals comparisons, etc.), in order to find objective associations and/or correlations in their findings. For instance, it would be very important to know if there were any statistically significant correlations between the replies of survey participants and their age, job/specialty and work experience.",
                Arrays.asList(new HighlightSectionForm(92, 1174, 51, 16)));
        List<InlineCommentForm> a3e3i = Arrays.asList(a2e3i1, a2e3i2, a2e3i3);

        expertReviewService.postExpertReview(savedA2.getId(), new ExpertReviewForm(a2UserExpert3.getUsername(),
                "This is an interesting manuscript reporting findings from a local survey on the expectations of healthcare professionals about the use of AI in healthcare. However, it has substantial flaws that should be addressed.\n\nIs the work clearly and accurately presented and does it cite the current literature?\nPartly\n\nIs the study design appropriate and is the work technically sound?\nPartly\n\nAre sufficient details of methods and analysis provided to allow replication by others?\nPartly\n\nIf applicable, is the statistical analysis and its interpretation appropriate?\nNot applicable\n\nAre all the source data underlying the results available to ensure full reproducibility?\nYes\n\nAre the conclusions drawn adequately supported by the results?\nPartly",
                "Rejected", a3e3i));

        // Add article 3
        Article a3 = new Article(
                "Oncoyeasti: a web-based application to translate data obtained from Saccharomyces cerevisiae high-throughput drug screens into cancer therapeutics",
                "The budding yeast (Saccharomyces cerevisiae) gene deletion library consists of a collection of more than 6,000 gene-deletion mutants and is useful for high-throughput screening of anti-cancer drugs. Because of the shorter doubling time and the significant homology the budding yeast   shares with human cells, using a high-throughput chemical screen of budding yeast gene deletion library, one can rapidly identify various genetic targets of anti-cancer drugs. But analyzing the data derived from a yeast library screen to identify corresponding human homologs and their status in various cancers is a cumbersome process. We have developed a web-based app, Oncoyeasti, which enables the researcher to automatically identify the corresponding human homologs of S. cerevisiae and the status of these homologs genes in tumor samples from The Cancer Genome Atlas Database and cell line samples from the Cancer Cell Line Encyclopedia. This would enable the scientists to identify the tumors and choose cell lines to work on and thus serve as an indispensable tool to translate their research into human cancers.",
                "Biology", "Medicine",
                "https://objectstorage.us-ashburn-1.oraclecloud.com/n/idvpzhveofap/b/research-exchange-pdf-storage-bucket/o/Biology_Medicine_Cancer_Trimmed.pdf",
                "https://github.com/oncoadmn/oncoyeasti.org",
                "https://www.cancer.gov/about-nci/organization/ccg/research/structural-genomics/tcga");
        a3.setScore(-23);
        Article savedA3 = articleRepository.save(a3);

        User a3AuthorUser1 = new User("Ruby Gupta", "rgupta", "rgupta@gmail.com", hashedPassword, salt, "User");
        User a3AuthorUser2 = new User("Samir Cayenne", "scayenne", "scayenne@gmail.com", hashedPassword, salt,
                "User");
        User a3AuthorUser3 = new User("Madhu Dyavaiah", "mdyavaiah", "mdyavaiah@gmail.com", hashedPassword,
                salt,
                "User");
        User a3AuthorUser4 = new User("Pragnya Srinivas", "psrinivas", "psrinivas@gmail.com", hashedPassword,
                salt,
                "User");
        User a3AuthorUser5 = new User("David Otohinoyi", "dotohinoyi", "dotohinoyi@gmail.com", hashedPassword,
                salt,
                "User");
        User a3AuthorUser6 = new User("Debjyoti Talukdar", "dtalukdar", "dtalukdar@gmail.com", hashedPassword,
                salt,
                "User");
        User a3AuthorUser7 = new User("Chidambra Halari", "chalari", "chalari@gmail.com", hashedPassword, salt,
                "User");
        User a3AuthorUser8 = new User("Ashok Ramani", "aramani", "aramani@gmail.com", hashedPassword, salt,
                "User");
        User a3AuthorUser9 = new User("Joshua Yusuf", "jyusuf", "jyusuf@gmail.com", hashedPassword, salt,
                "User");
        User a3AuthorUser10 = new User("Khushdeep Chahal", "kchahal", "kchahal@gmail.com", hashedPassword, salt,
                "User");
        User a3AuthorUser11 = new User("Rupinder Kaur", "rkaur", "rkaur@gmail.com", hashedPassword, salt,
                "User");
        User a3AuthorUser12 = new User("Ankit Patel", "apatel", "apatel@gmail.com", hashedPassword, salt,
                "User");
        User a3AuthorUser13 = new User("Avaniben Patel", "apatel2", "apatel2@gmail.com", hashedPassword, salt,
                "User");
        User a3AuthorUser14 = new User("Ravindrasingh Rajput", "rrajput", "rrajput@gmail.com", hashedPassword,
                salt,
                "User");
        User a3AuthorUser15 = new User("Harish Siddaiah", "hsiddaiah", "hsiddaiah@gmail.com", hashedPassword,
                salt,
                "User");
        User a3AuthorUser16 = new User("Shilpadevi Patil", "spatil", "spatil@gmail.com", hashedPassword, salt,
                "User");
        User a3AuthorUser17 = new User("Ashish Patil", "apatil", "apatil@gmail.com", hashedPassword, salt,
                "User");
        User a3AuthorUser18 = new User("Nikhilesh Anand", "nanand", "nanand@gmail.com", hashedPassword, salt,
                "User");
        User a3AuthorUser19 = new User("Avaniben Patel", "apatel3", "apatel3@gmail.com", hashedPassword, salt,
                "User");

        userRepository.saveAll(Arrays.asList(a3AuthorUser1, a3AuthorUser2, a3AuthorUser3, a3AuthorUser4,
                a3AuthorUser5, a3AuthorUser6, a3AuthorUser7, a3AuthorUser8, a3AuthorUser9,
                a3AuthorUser10, a3AuthorUser11, a3AuthorUser12, a3AuthorUser13, a3AuthorUser14,
                a3AuthorUser15, a3AuthorUser16, a3AuthorUser17, a3AuthorUser18, a3AuthorUser19));

        Author a3Author1 = new Author("rgupta", savedA3);
        Author a3Author2 = new Author("scayenne", savedA3);
        Author a3Author3 = new Author("mdyavaiah", savedA3);
        Author a3Author4 = new Author("psrinivas", savedA3);
        Author a3Author5 = new Author("dotohinoyi", savedA3);
        Author a3Author6 = new Author("dtalukdar", savedA3);
        Author a3Author7 = new Author("chalari", savedA3);
        Author a3Author8 = new Author("aramani", savedA3);
        Author a3Author9 = new Author("jyusuf", savedA3);
        Author a3Author10 = new Author("kchahal", savedA3);
        Author a3Author11 = new Author("rkaur", savedA3);
        Author a3Author12 = new Author("apatel", savedA3);
        Author a3Author13 = new Author("apatel2", savedA3);
        Author a3Author14 = new Author("rrajput", savedA3);
        Author a3Author15 = new Author("hsiddaiah", savedA3);
        Author a3Author16 = new Author("spatil", savedA3);
        Author a3Author17 = new Author("apatil", savedA3);
        Author a3Author18 = new Author("nanand", savedA3);
        Author a3Author19 = new Author("apatel3", savedA3);
        authorRepository.saveAll(Arrays.asList(a3Author1, a3Author2, a3Author3, a3Author4, a3Author5, a3Author6,
                a3Author7, a3Author8, a3Author9, a3Author10, a3Author11, a3Author12, a3Author13,
                a3Author14, a3Author15,
                a3Author16, a3Author17, a3Author18, a3Author19));

        Tag a3Tag1 = new Tag("Software Tool", savedA3);
        Tag a3Tag2 = new Tag("Cancer", savedA3);
        tagRepository.saveAll(Arrays.asList(a3Tag1, a3Tag2));

        Link a3Link1 = new Link("https://sites.broadinstitute.org/ccle", savedA3);
        Link a3Link2 = new Link(
                "http://www-sequence.stanford.edu/group/yeast_deletion_project/project_desc.html",
                savedA3);
        Link a3Link3 = new Link("http://www.oncoyeasti.org/", savedA3);
        Link a3Link4 = new Link("https://f1000research.com/articles/7-757", savedA3);
        linkRepository.saveAll(Arrays.asList(a3Link1, a3Link2, a3Link3, a3Link4));

        User a3UserExpert1 = new User("Ayaz Najafov", "anajafo", "anajafo@gmail.com", hashedPassword, salt,
                "Expert");
        User a3UserExpert2 = new User("Alvaro Galli", "agalli", "agalli@gmail.com", hashedPassword, salt,
                "Expert");
        userRepository.saveAll(Arrays.asList(a3UserExpert1, a3UserExpert2));

        expertRepository.saveAll(
                Arrays.asList(new Expert("anajafo", "Biology"),
                        new Expert("agalli", "Computer Science")));

        InlineCommentForm a3e1i1 = new InlineCommentForm(3, "MSQL", "should be either \"MySQL\" or \"mSQL\"",
                Arrays.asList(new HighlightSectionForm(408, 731, 41, 16)));
        InlineCommentForm a3e1i2 = new InlineCommentForm(3, "cross cancer", "should be \"cross-cancer\"",
                Arrays.asList(new HighlightSectionForm(201, 695, 77, 16)));
        InlineCommentForm a3e1i3 = new InlineCommentForm(3, "humologs", "should be \"homologs\"",
                Arrays.asList(new HighlightSectionForm(92, 623, 60, 16)));
        InlineCommentForm a3e1i4 = new InlineCommentForm(3, "feature rich", "should be \"feature-rich\"",
                Arrays.asList(new HighlightSectionForm(835, 369, 68, 16)));
        InlineCommentForm a3e1i5 = new InlineCommentForm(2,
                "con-taining only 10% of the DNA of human chromosomes",
                "should be rephrased",
                Arrays.asList(new HighlightSectionForm(880, 464, 26, 16),
                        new HighlightSectionForm(513, 482, 318, 16)));
        InlineCommentForm a3e1i6 = new InlineCommentForm(1, "BRAFV600E amplification",
                "should be \"BRAF-V600E mutation\", since the V600E mutation is not an amplification",
                Arrays.asList(new HighlightSectionForm(252, 877, 166, 16)));
        InlineCommentForm a3e1i7 = new InlineCommentForm(1,
                ". One of the mechanisms attributed to thedevelopment of resistance is intra-tumor heterogeneity",
                "Since Oncoyeasti does not address the cancer heterogeneity directly, that part of the introduction seems unimportant for the manuscript.",
                Arrays.asList(new HighlightSectionForm(229, 606, 256, 16),
                        new HighlightSectionForm(92, 624, 310, 16)));
        InlineCommentForm a3e1i8 = new InlineCommentForm(1, "Introduction",
                "should be as concise and as relevant as possible",
                Arrays.asList(new HighlightSectionForm(92, 135, 90, 18)));
        InlineCommentForm a3e1i9 = new InlineCommentForm(1, "cancer, treatment",
                "should be \"cancer treatment\"",
                Arrays.asList(new HighlightSectionForm(347, 155, 105, 16)));
        List<InlineCommentForm> a3e1i = Arrays.asList(a3e1i1, a3e1i2, a3e1i3, a3e1i4, a3e1i5, a3e1i6, a3e1i7,
                a3e1i8,
                a3e1i9);

        ExpertReviewDTO a3r1 = expertReviewService.postExpertReview(savedA3.getId(), new ExpertReviewForm(
                a3UserExpert1.getUsername(),
                "The authors have developed a web tool called Oncoyeasti, which bridges the gap between yeast and human ortholog gene symbols, and allows for easier submission of the queried genes to the TCGA/cBioportal database search. While this software can be useful, the following major issues have to be addressed:\n\n- Oncoyeasti should be able to retrieve the full list of human homologs that match to the yeast orthologs. For instance, if Sch9 is an ortholog of the Akt family, the output should be Akt1, Akt2, Akt3, rather than Akt3 only.\n\n- Oncoyeasti does not seem to retrieve some human orthologs accurately. For example, when TOR1 and TOR2 are queried, TOR1 matches to SMG1, while TOR2 matches to mTOR. In reality, both yeast TOR1 and yeast TOR2 should match to human mTOR. Additionally, PKC1, YPK1, and YPK2 all match to Akt3, but they should match to PRKCE and SGK2 instead. Thus, the Oncoyeasti database needs to be improved for accurate yeast-to-human gene symbol matching. The NCBI's Homologene database can be employed for this purpose.\n\n- If a certain yeast gene does not have a human ortholog, this should be reported in the output of Oncoyeasti (instead of only reporting those that have a human ortholog and skipping those that don't have a human ortholog).\n\nIs the rationale for developing the new software tool clearly explained?\nYes\n\nIs the description of the software tool technically sound?\nYes\n\nAre sufficient details of the code, methods and analysis (if applicable) provided to allow replication of the software development and its use by others?\nYes\n\nIs sufficient information provided to allow interpretation of the expected output datasets and any results generated using the tool?\nYes\n\nAre the conclusions about the tool and its performance adequately supported by the findings presented in the article?\nNo",
                "Needs Work", a3e1i));

        commentService.postComment(a3.getId(), new CommentForm(a3r1.getId(), a3r1.getId(),
                a3AuthorUser1.getUsername(),
                "Initially we had used a homolog gene list which was provided to us, by www.yeastgenome.org (Stanford database), but we did not know that this list was not complete and was missing significant number of human homologs. Thank you for bringing this to our notice. Now we used http://useast.ensembl.org/biomart/martview/ba508c9593727fcc0111904d444c5296 and generated the list for human homologs for S Cerevisiae. This is the list we generated http://useast.ensembl.org/biomart/martresults/148?file=martquery_0621224014_815.xls.gz.\n\nThen we compared ensemble list with our stanford database list, to do corrections and also to add to our database, the  additional s cerevisiae genes and their human homologs, which were missing in our list, that we got from yeast genome.org. Using ensemble biomart, we have added 4157 additional human homologs to our existing list which we got from yeastgenome.org and thus making oncoyeasti much more comprehensive."));

        expertReviewService.postExpertReview(savedA3.getId(), new ExpertReviewForm(a3UserExpert2.getUsername(),
                "The web-based application, described in the manuscript, could be relevant in terms of applicability in cancer therapy because has the potentiality to find new targets or new factors affecting drug response. The starting point is homology between yeast and human. The authors only showed the RAD9 as example and reported that the human counterpart is the TP53BP1 gene. Where did they find this information? To my knowledge, yeast RAD9 has no human homolog; I have checked in the yeast genome database (https://www.yeastgenome.org/) and I could not find any homology. TP53BP1 appears to be RAD9 homolog only if you find homoly with no filted application (PANTHER method only giving this result, 1 out of 10) I guess that the authors has to state how and where the human homologous to the yeast gene are found and show a little more examples. Moreover, the application only shows data from cBioPortal with no statistical evaluation on how it would be relevant to translate study form yeast to human. What I mean is that there is no functional evaluation of the gene, no data about the level of conservation between human and yeast. What I think is that a high level of conservation should correspond a high degree of reliability. Recently, Mercatanti et al (FEMS Yeast Yesearch, Dec. 2017) published a web tool where yeast strains could be \"humanized\" and evaluated the reliability score to make the functional assay more relevant for cancer risk evaluation. I do not think that the manuscript is not acceptable in the present form.\n\nIs the rationale for developing the new software tool clearly explained?\nNo\n\nIs the description of the software tool technically sound?\nNo\n\nAre sufficient details of the code, methods and analysis (if applicable) provided to allow replication of the software development and its use by others?\nNo\n\nIs sufficient information provided to allow interpretation of the expected output datasets and any results generated using the tool?\nNo\n\nAre the conclusions about the tool and its performance adequately supported by the findings presented in the article?\nNo",
                "Rejected", null));

        // Add article 4
        Article a4 = new Article("The common patterns of abundance: the log series and Zipf's law",
                "In a language corpus, the probability that a word occurs n times is often proportional to 1/(n^2). Assigning rank, s, to words according to their abundance, log(s) vs log(n) typically has a slope of minus one. That simple Zipf's law pattern also arises in the population sizes of cities, the sizes of corporations, and other patterns of abundance. By contrast, for the abundances of different biological species, the probability of a population of size n is typically proportional to 1/n, declining exponentially for larger n, the log series pattern.\n\nThis article shows that the differing patterns of Zipf's law and the log series arise as the opposing endpoints of a more general theory. The general theory follows from the generic form of all probability patterns as a consequence of conserved average values and the associated invariances of scale.\n\nTo understand the common patterns of abundance, the generic form of probability distributions plus the conserved average abundance is sufficient. The general theory includes cases that are between the Zipf and log series endpoints, providing a broad framework for analyzing widely observed abundance patterns.",
                "Mathematics", "Probability",
                "https://objectstorage.us-ashburn-1.oraclecloud.com/n/idvpzhveofap/b/research-exchange-pdf-storage-bucket/o/math.pdf",
                null, "https://zenodo.org/record/2597896#.YmWdk9rMJPY");
        a4.setScore(146);
        Article savedA4 = articleRepository.save(a4);

        User a4AuthorUser = new User("Steven A. Frank", "afrank", "afrank@gmail.com", hashedPassword, salt,
                "User");

        userRepository.save(a4AuthorUser);

        Author a4Author = new Author("afrank", savedA4);
        authorRepository.save(a4Author);

        Link a4Link = new Link("https://f1000research.com/articles/8-334", savedA4);
        linkRepository.save(a4Link);

        User a4UserExpert1 = new User("Luis M. A. Bettencourt", "lbetten", "lbetten@gmail.com", hashedPassword,
                salt,
                "Expert");
        User a4UserExpert2 = new User("Jose Lobo", "jlobo", "jlobo@gmail.com", hashedPassword, salt, "Expert");
        User a4UserExpert3 = new User("Scott E. Page", "spage", "spage@gmail.com", hashedPassword, salt,
                "Expert");
        userRepository.saveAll(Arrays.asList(a4UserExpert1, a4UserExpert2, a4UserExpert3));

        Expert a4Expert1 = new Expert("lbetten", "Mathematics");
        Expert a4Expert2 = new Expert("jlobo", "Mathematics");
        Expert a4Expert3 = new Expert("spage", "Mathematics");
        expertRepository.saveAll(Arrays.asList(a4Expert1, a4Expert2, a4Expert3));

        InlineCommentForm a4e1i1 = new InlineCommentForm(1, "conditions",
                "It would be interesting to describe the conditions (in terms of beta and any limits or time dependence on averages) for deriving the third distribution often invoked to describe the same abundances, the log-normal, in terms of the reasoning about invariances advanced here. This is discussed to some extent in previous work by the author. I think its inclusion and discussion would benefit the current manuscript.",
                Arrays.asList(new HighlightSectionForm(415, 259, 66, 16)));
        InlineCommentForm a4e1i2 = new InlineCommentForm(1, "Theory",
                "The relationship between population dynamics and invariances of the abundance (or rate) distributions could be made a little more explicit: Population dynamics models (in analogy to other dynamical systems) are mappings, tracing explicit variable transformation over time, such as changes of \"position\" (translations, r-> a + r), or dilations (r -> b r). Asking for invariances of distributions under these dynamical transformations is the usual way to derive the distributions as steady state abundances. Power laws, such as Zipf's law, are invariant under (stochastic) dilations, for example, while Fisher's log series are invariant under other simple types of population dynamics. I'd appreciate a bit more discussion bridging these two approaches.",
                Arrays.asList(new HighlightSectionForm(92, 1069, 51, 18)));
        InlineCommentForm a4e1i3 = new InlineCommentForm(1, "beta -> 0",
                "As, the author shows the derivation of Zipf's law requires not only a parameter choice (beta -> 0) but also the limit of the average abundance -> infinity. Without the latter, the power law exponent won't be Zipf's. In dynamical derivations of Zipf's law one asks instead that geometric random motion of the population abundances, is subjected to a (\"reflecting\") boundary condition for small population sizes that stops them from getting too small. Under what circumstances are these two additional requirements (besides transformational invariances under multiplicative growth) equivalent? They seem to have a different character as one is a limit, while the other a boundary condition - is the limiting condition on the average the most general condition?",
                Arrays.asList(new HighlightSectionForm(588, 1123, 8, 19),
                        new HighlightSectionForm(596, 1123, 0, 14),
                        new HighlightSectionForm(596, 1123, 0, 19),
                        new HighlightSectionForm(603, 1124, 12, 14),
                        new HighlightSectionForm(603, 1125, 12, 14),
                        new HighlightSectionForm(616, 1124, 0, 14),
                        new HighlightSectionForm(616, 1126, 0, 14),
                        new HighlightSectionForm(623, 1125, 7, 16)));
        List<InlineCommentForm> a4e1i = Arrays.asList(a4e1i1, a4e1i2, a4e1i3);

        ExpertReviewDTO a4r1 = expertReviewService.postExpertReview(savedA4.getId(), new ExpertReviewForm(
                a4UserExpert1.getUsername(),
                "This manuscript approaches the origins of two particularly important distributions describing abundances in biological and social populations from the perspective of mathematical invariances of their mathematical forms. The author shows in this way that Fisher's log series distribution and Zipf's \"law\" can arise in different limits of the same parameter, characterizing a family of affine transformations that includes translations and scale transformations of growth rates. The mathematical derivation is clear and elegant, so that the manuscript makes an important contribution to formal models deriving these abundance distributions.\n\nWhat I think would improve the manuscript is greater contact with other methods for the derivation of these same distributions of abundance and an expanded discussion of limits.\n\nIs the work clearly and accurately presented and does it cite the current literature?\nYes\n\nIs the study design appropriate and is the work technically sound?\nYes\n\nAre sufficient details of methods and analysis provided to allow replication by others?\nYes\nnIf applicable, is the statistical analysis and its interpretation appropriate?\nYes\n\nAre all the source data underlying the results available to ensure full reproducibility?\nNo source data required\n\nAre the conclusions drawn adequately supported by the results?\nPartly",
                "Approved", a4e1i));

        commentService.postComment(a4.getId(), new CommentForm(a4r1.getId(), a4r1.getId(),
                a4AuthorUser.getUsername(),
                "I appreciate the thoughtful comments from Luis Bettencourt. The review and my reply are part of the final published version. So I will use this space to develop my comments, leaving the original manuscript unchanged."));

        commentService.postComment(a4.getId(), new CommentForm(a4r1.getInlineComments().get(0).getId(),
                a4r1.getInlineComments().get(0).getId(),
                a4AuthorUser.getUsername(),
                "I have a new unpublished manuscript that unifies the log series, Zipf's law, and the lognormal. A single additional invariance leads to a unified distribution that includes all of those distributions as special cases and also some intermediate forms that commonly arise in certain empirical examples. Thus, I agree with the importance of this comment, but withhold further details until I can complete my new work."));

        commentService.postComment(a4.getId(), new CommentForm(a4r1.getInlineComments().get(1).getId(),
                a4r1.getInlineComments().get(1).getId(),
                a4AuthorUser.getUsername(),
                "I agree that connecting dynamic models of process to the invariances that set pattern is the great missing piece in this work. Ultimately, an invariance perspective can only achieve its full usefulness if one can compare and empirically test alternative hypotheses about mechanistic processes. That might, for example, require one to identify the particular aspect within a mechanistic set of processes that ultimately defines the invariance that dominates pattern. Then, by comparing different mechanistic models, one can reduce that comparison to the contrast between alternative component processes that dominate invariance, and so develop a more focused empirical test. I am not yet certain how to make these connections between abstract invariances and mechanistic dynamical models in the most meaningful way, so I have refrained from writing about these issues. This is a primary topic for future work."));

        commentService.postComment(a4.getId(), new CommentForm(a4r1.getInlineComments().get(2).getId(),
                a4r1.getInlineComments().get(2).getId(),
                a4AuthorUser.getUsername(),
                "First, one just needs average abundance to be not small to get an exponent that is essentially equivalent to Zipf's law and sufficient for empirical comparison. Second, I agree that it would be useful to consider the relations between boundary conditions in dynamics and simplified invariance models. I don't know the answer. It would be a useful topic for future work."));

        InlineCommentForm a4e2i1 = new InlineCommentForm(3,
                "This approach reveals thesimple invariant structure of many common probabilitypatterns.",
                "The usefulness and scope of the conclusion would be strengthened if the author considered another distribution which arises often in the explorations of growth processes: the log normal. It would also strengthen the usefulness of the manuscript if the author were to expand on this, in particular recapitulating what is the invariant structure.",
                Arrays.asList(new HighlightSectionForm(309, 330, 159, 16),
                        new HighlightSectionForm(92, 348, 329, 16),
                        new HighlightSectionForm(92, 366, 50, 16)));
        InlineCommentForm a4e2i2 = new InlineCommentForm(3,
                "two great and seemingly uncon-nected puzzles solve very simply in terms of a single continuumbetween alternative invariances.",
                "This approach reveals the simple invariant structure of many common probability patterns.\" clearly follows from the exposition and is a useful contribution.",
                Arrays.asList(new HighlightSectionForm(269, 294, 176, 16),
                        new HighlightSectionForm(92, 312, 393, 16),
                        new HighlightSectionForm(92, 330, 135, 16)));
        InlineCommentForm a4e2i3 = new InlineCommentForm(1, "Theory",
                "The mathematical derivations are clear.",
                Arrays.asList(new HighlightSectionForm(92, 1069, 51, 18)));
        InlineCommentForm a4e2i4 = new InlineCommentForm(3, "Conclusion",
                "Having connected two widely used distributions, what sort of research questions can now be addressed? How can the invariant structure linking two distributions be used in contexts other than Zipf's law or species distributions?",
                Arrays.asList(new HighlightSectionForm(92, 275, 84, 18)));
        List<InlineCommentForm> a4e2i = Arrays.asList(a4e2i1, a4e2i2, a4e2i3, a4e2i4);

        ExpertReviewDTO a4r2 = expertReviewService.postExpertReview(savedA4.getId(), new ExpertReviewForm(
                a4UserExpert2.getUsername(),
                "The manuscript addresses the relationship between two probability distributions that, although originated in specific research domains, have gone on to be widely used as representations of growth processes.\n \nIs the work clearly and accurately presented and does it cite the current literature?\nYes\n\nIs the study design appropriate and is the work technically sound?\nYes\n\nAre sufficient details of methods and analysis provided to allow replication by others?\nYes\n\nIf applicable, is the statistical analysis and its interpretation appropriate?\nYes\n\nAre all the source data underlying the results available to ensure full reproducibility?\nYes\n\nAre the conclusions drawn adequately supported by the results?\nYes",
                "Approved", a4e2i));

        commentService.postComment(a4.getId(), new CommentForm(a4r2.getId(), a4r2.getId(),
                a4AuthorUser.getUsername(),
                "I appreciate the thoughtful comments from Jose Lobo. The review and my reply are part of the final published version. So I will use this space to develop my comments, leaving the original manuscript unchanged."));

        commentService.postComment(a4.getId(), new CommentForm(a4r2.getInlineComments().get(0).getId(),
                a4r2.getInlineComments().get(0).getId(),
                a4AuthorUser.getUsername(),
                "Copying my reply to Luis Bettencourt's comment on the same topic: I have a new unpublished manuscript that unifies the log series, Zipf's law, and the lognormal. A single additional invariance leads to a unified distribution that includes all of those distributions as special cases and also some intermediate forms that commonly arise in certain empirical examples. Thus, I agree with the importance of this comment, but withhold further details until I can complete my new work. I agree that the current manuscript is rather terse about this issue. However, I have written several prior manuscripts that extensively develop this aspect, cited in the current publication. I prefer to keep the current manuscript short and focused on the new aspect related to the log series and Zipf's law, and refer to earlier publications for the background."));

        commentService.postComment(a4.getId(), new CommentForm(a4r2.getInlineComments().get(3).getId(),
                a4r2.getInlineComments().get(3).getId(),
                a4AuthorUser.getUsername(),
                "I think the key advance will not come until more can be said about linking dynamic models to the abstract invariant structures. Copying my reply to Luis Bettencourt's review: I agree that connecting dynamic models of process to the invariances that set pattern is the great missing piece in this work. Ultimately, an invariance perspective can only achieve its full usefulness if one can compare and empirically test alternative hypotheses about mechanistic processes. That might, for example, require one to identify the particular aspect within a mechanistic set of processes that ultimately defines the invariance that dominates pattern. Then, by comparing different mechanistic models, one can reduce that comparison to the contrast between alternative component processes that dominate invariance, and so develop a more focused empirical test. I am not yet certain how to make these connections between abstract invariances and mechanistic dynamical models in the most meaningful way, so I have refrained from writing about these issues. This is a primary topic for future work."));

        ExpertReviewDTO a4r3 = expertReviewService.postExpertReview(savedA4.getId(),
                new ExpertReviewForm(
                        a4UserExpert3.getUsername(),
                        "I found this article to be fascinating and elucidating but also a bit frustrating to read. The central claim of the article is that one can construct a family of distributions such that Zipf's law and species abundance are the endpoints of a more general process. For that result to be interesting, the result has to be for a general process and not for a family of distributions. The latter is easy. I just say, \"here is a family of distributions, f(x) = x^(-a)\" and then say that at one endpoint a=1 I have a species area law and at the other endpoint a=2, I get Zipf's law. The contribution of the article lies in convincing us that the paper has done something other than an elaborate change of variables that simply restates that result through obfuscation.\n\nSo what does the paper do? The paper shows that if we restrict attention to probability patterns (by the way, it would be nice if \"pattern\" were formally defined) that are invariant to affine transformations then we have a specific form given by equation (3). Given the form in equation (3), the author then claims that n represents pattern and r represents process. This needs to be elucidated.\n\nFor the main result, once we have invariance to affine transformation we get the differential equation with dT_z/dw = alpha + beta T_z. From here, why doesn't it just follow that if beta = 0, we have something that's going to fall off with a common invariant scale and for bbeta = 1 the invariant scale changes with n.\n\nThe conclusion of the paper needs to be expanded. As a reader, I need a richer explanation for how the approach \"reveals the simple invariant structure\" of common probability distributions. In the conclusion, we should be given more intuition for how the holding the average abundance constant drives the results. Also, it would be nice to have more insight into what would cause a system to have more or fewer proportional processes acting on it.\n\nQuibble:\nWhy isn't r a function of tau - the period of time?\n\nIs the work clearly and accurately presented and does it cite the current literature?\nYes\n\nIs the study design appropriate and is the work technically sound?\nYes\n\nAre sufficient details of methods and analysis provided to allow replication by others?\nYes\n\nIf applicable, is the statistical analysis and its interpretation appropriate?\nYes\n\nAre all the source data underlying the results available to ensure full reproducibility?\nYes\n\nAre the conclusions drawn adequately supported by the results?\nYes",
                        "Approved", null));

        commentService.postComment(a4.getId(), new CommentForm(a4r3.getId(), a4r3.getId(),
                a4AuthorUser.getUsername(),
                "I appreciate the thoughtful comments from Scott Page. The review and my reply are part of the final published version. So, I will use this space to develop my comments, leaving the original manuscript unchanged.\n\n\"For that result to be interesting, the result has to be for a general process and not for a family of distributions.\"\n\nI partially agree. It is very interesting to understand how a general process relates to a family of distributions. I am currently pursuing that by studying the species abundance problem in ecology in more detail as an example. In my new work, I show how various well known general processes, such as neutral theory, relate to an abstract family of distributions characterized by simple invariances. My new work will show a much simpler way in which to understand the relations between process and pattern in ecology than is currently the case in the literature of that subject. I think that new work will help a bit with regard to this question, because the log series and Zipf's law are special cases of a broader family of distributions that also includes the lognormal. The current article is a step on the way to the more ambitious study. However, I also partially disagree. Identifying the invariant structure of probability patterns is by itself useful. It guides one in more focused projects and provides a way to understand what is expected and what is not. Further, one can specify a general process that leads to a Gaussian distribution, but that process will not be a full understanding of the Gaussian distribution, just one instance of a process that associates with that pattern. There are other general processes that are distinct but also converge to the Gaussian. So, we need to understand both the different types of general process and the abstract aspects of the Gaussian that unify all conforming general processes. The current article is entirely on the abstract side. That is a necessary but not sufficient component. I agree that more needs to be done and am working on that in the ecology application mentioned above. I hope to contribute along those lines in my future work.\n\n\"So what does the paper do? The paper shows that if we restrict attention to probability patterns (by the way, it would be nice if \"pattern\" were formally defined) ...\"\n\nNo widely agreed definition of \"pattern\" exists, which is interesting. I believe that \"pattern\" and \"invariance\" are the same thing, but that remains an open issue.\n\n\"Given the form in equation (3), the author then claims that n represents pattern and ...\"\n\nMy use of n as pattern simply describes what people have typically measured and reported as a pattern. In other words, people have measured population sizes and reported those data as a pattern. My claim that r corresponds to process reflects the general agreement that populations change by birth, death, migration, and other processes that act multiplicatively, again a description of the common understanding. For example, the neutral theory in ecology, which has become a dominant approach to the study of ecological process, is about demographic processes that act proportionally or multiplicatively. So, both by intuition and by consensus, I have adopted r as associated with process."));

        commentService.postComment(a4.getId(), new CommentForm(a4r3.getId(), a4r3.getId(),
                a4AuthorUser.getUsername(),
                "\"From here, why doesn't it just follow that if beta = 0, we have something that's going to fall off with a common invariant scale and for beta = 1 the invariant scale changes with n.\"\n\nI do not understand the question. The point of the differential equation is that we can find a new scale, w, that is shift invariant but not stretch invariant with respect to probability pattern. As beta goes to zero, w becomes both shift and stretch invariant (affine invariant). It turns out that beta is a sort of curvature that describes departure from stretch (multiplicative) invariance. As an observation, working with w has turned out to provide a key way in which to unify diverse probability distributions within a single common system, suggesting an invariant structure that unifies commonly observed probability patterns. That was the topic of several of my prior publications. Here, I was just using some of the prior insight to try and understand the nature of the log series and Zipf's law and perhaps something about how those distributions arise. I will expand on that in a future manuscript, see next comment.\n\n\"The conclusion of the paper needs to be expanded. As a reader, I need a richer explanation ...\"\n\nFirst, my prior publications discuss invariant structure and its consequences in an abstract way. But I think that is not the issue that is being asked about here. So, second, I am currently finishing a new manuscript that extends this current article in several ways. My new manuscript focuses on invariance in ecological pattern. By emphasizing a particular application and its associated literature, I am able more explicitly to get at some of the issues that are too vague in the current manuscript. For example, I will consider directly the role of average values by relating maximum entropy and invariance approaches explicitly in the context of ecological applications. I think this will help some. Many of the other comments raised by the reviewers also come up in the new work, suggesting that there are some obvious missing steps here that need further attention. These issues cannot be resolved in a few paragraphs, so I am going to wait until I finish the new work before trying to address these problems. I apologize for putting off thoughtful and important comments, but I need more time to complete the new work before I can give good answers.\n\n\"Why isn't r a function of tau-the period of time?\"\n\nIt is. Whether that matters depends on the particular question. One aspect is that, as long as tau is taken as a fixed value, such as generation time in a discrete generation model of populations, then one can take r directly without concern about varying tau. Alternatively, if one has reason to consider tau as varying, then it may matter for certain aspects. I have not looked into that. I agree that it would be worthwhile to understand this issue more clearly."));

        // Add article 5
        Article a5 = new Article(
                "A grammatico-pragmatic analysis of the because X construction: Private expression within public expression",
                "Background: This article investigates an innovative use of because, called the because X construction (e.g., because homework). Quantitative and qualitative research as well as research about the historical development of the construction have been conducted. The present article aims to determine what motivates the use of the construction.\nMethods: Based on the data collected from the literature and online sources, the grammar of the because X construction is described in detail. The construction is then analyzed  within Hirose's (2015) three-tier model of language use.\nResults: A two-layered expressive structure is proposed: The X-element serves as a private expression, which is a speaker's expression of thought with no intention of communication, whereas the whole construction functions publicly. The private nature of the X-element consistently accounts for the syntactic categories of the X-element and the restrictions on them observed in the literature.\nConclusion: The proposed two-layered expressive structure reflects a metapragmatic function of the construction. A private, subjective expression embedded in a public expression has the function of connecting the hearer to the speaker, and it accordingly brings about a joint attention effect. With such a function, the proposed structure is effective especially (but not exclusively) in online communication because one can strategically indicate closeness or intimacy to others, particularly in an environment where nonverbal means are difficult to apply.",
                "Art", "Literature",
                "https://objectstorage.us-ashburn-1.oraclecloud.com/n/idvpzhveofap/b/research-exchange-pdf-storage-bucket/o/art.pdf",
                null, null);
        a5.setScore(47);
        Article savedA5 = articleRepository.save(a5);

        User a5AuthorUser = new User("Masaru Kanetani", "mkaneta", "mkaneta@gmail.com", hashedPassword, salt,
                "User");

        userRepository.save(a5AuthorUser);

        Author a5Author = new Author("afrank", savedA5);
        authorRepository.save(a5Author);

        Tag a5Tag1 = new Tag("Grammar", savedA5);
        Tag a5Tag2 = new Tag("Public Expression", savedA5);
        Tag a5Tag3 = new Tag("Private Expression", savedA5);
        tagRepository.saveAll(Arrays.asList(a5Tag1, a5Tag2, a5Tag3));

        Link a5Link = new Link("https://f1000research.com/articles/10-965", savedA5);
        linkRepository.save(a5Link);

        User a5UserExpert1 = new User("Axel Bohmann", "abohman", "abohman@gmail.com", hashedPassword, salt,
                "Expert");
        User a5UserExpert2 = new User("Bert Cappelle", "bcapell", "bcapell@gmail.com", hashedPassword, salt,
                "Expert");
        userRepository.saveAll(Arrays.asList(a5UserExpert1, a5UserExpert2));

        Expert a5Expert1 = new Expert("abohman", "Art");
        Expert a5Expert2 = new Expert("bcapell", "Art");
        expertRepository.saveAll(Arrays.asList(a5Expert1, a5Expert2));

        expertReviewService.postExpertReview(savedA5.getId(),
                new ExpertReviewForm(a5UserExpert1.getUsername(), "review", "Approved", null));

        expertReviewService.postExpertReview(savedA5.getId(),
                new ExpertReviewForm(a5UserExpert2.getUsername(), "review", "Approved", null));

        // Add article 6
        Article a6 = new Article(
                "Measuring the motives of informal entrepreneurs",
                "Background - Handling non-observed activities pose major challenges to the governments and other stakeholders. Non-observed activities refer to underground activities, illegal activities, informal sector and any other activities that result in goods or services consumed by the household. The impact of these non-observed activities shows that the volume of people involved in the informal sector will rapidly increase. Informal economic activities are technically illegal yet are not intended as antisocial, thereby remaining acceptable to many individuals within the society. This research aimed to identify the factors that lead to entrepreneurial necessity and opportunity.\nMethods - The data of 51 respondents who were employed as informal entrepreneurs in Klang Valley areas in Malaysia was collected with the use of a questionnaire and convenient and proportionate sampling techniques. The data were analysed using SPSS software.\nResults - The two primary drivers of informal entrepreneurial activity were necessity and opportunity. The inability to find a formal job was an example of being driven by necessity. Meanwhile, individuals that are driven by opportunity chose to work independently in these informal sectors. Between necessity and engagement, refinement acted as a mediator. Often, necessity and opportunity do not automatically translate into successful entrepreneurship; further refinement is required in terms of market potential, technology usage, location preferences, and capital requirements. Improved refinement results in increased entrepreneurial engagement.\nConclusions - The role and contribution of the informal sector entrepreneurship in economic development need to be evaluated and not just observed as an opportunity for individuals who choose this type of career. Therefore, further research is required in a wider variety of contexts to evaluate whether the same remains true in different populations. The results of this study can be useful for the government to set policies to encourage the transition of informal to formal entrepreneurships in Malaysia.",
                "Business", "General/Other",
                "https://objectstorage.us-ashburn-1.oraclecloud.com/n/idvpzhveofap/b/research-exchange-pdf-storage-bucket/o/business.pdf",
                null, "https://figshare.com/s/c70b053eacb5ac8a24e4");
        a6.setScore(91);
        Article savedA6 = articleRepository.save(a6);

        User a6AuthorUser1 = new User("Noor Shahaliza Othman", "nothman", "nothman@gmail.com", hashedPassword,
                salt,
                "User");
        User a6AuthorUser2 = new User("Govindan Marthandan", "gmartha", "gmartha@gmail.com", hashedPassword,
                salt,
                "User");
        User a6AuthorUser3 = new User("Kamarulzaman Ab Aziz", "kaziz", "kaziz@gmail.com", hashedPassword, salt,
                "User");

        userRepository.saveAll(Arrays.asList(a6AuthorUser1, a6AuthorUser2, a6AuthorUser3));

        Author a6Author1 = new Author("nothman", savedA6);
        Author a6Author2 = new Author("gmartha", savedA6);
        Author a6Author3 = new Author("kaziz", savedA6);
        authorRepository.saveAll(Arrays.asList(a6Author1, a6Author2, a6Author3));

        Tag a6Tag = new Tag("Entrepreneurship", savedA6);
        tagRepository.save(a6Tag);

        Link a6Link1 = new Link("https://figshare.com/s/b9433876684c7fd89572", savedA6);
        Link a6Link2 = new Link("https://f1000research.com/articles/11-56", savedA6);
        linkRepository.saveAll(Arrays.asList(a6Link1, a6Link2));

        User a6UserExpert1 = new User("Gurel Cetin", "gcetin", "gcetin@gmail.com", hashedPassword, salt,
                "Expert");
        User a6UserExpert2 = new User("Muhammad Ashraf Fauzi", "mfauzi", "mfauzi@gmail.com", hashedPassword,
                salt,
                "Expert");
        userRepository.saveAll(Arrays.asList(a6UserExpert1, a6UserExpert2));

        Expert a6Expert1 = new Expert("gcetin", "Business");
        Expert a6Expert2 = new Expert("mfauzi", "Business");
        expertRepository.saveAll(Arrays.asList(a6Expert1, a6Expert2));

        expertReviewService.postExpertReview(savedA6.getId(),
                new ExpertReviewForm(a6UserExpert1.getUsername(), "review", "Rejected", null));

        expertReviewService.postExpertReview(savedA6.getId(),
                new ExpertReviewForm(a6UserExpert2.getUsername(), "review", "Needs Work", null));

        // Add article 7
        Article a7 = new Article(
                "Effect of blending ratio of wheat, orange fleshed sweet potato and haricot bean flour on proximate compositions, beta-carotene, physicochemical properties and sensory acceptability of biscuits",
                "Background: Protein-energy malnutrition and vitamin A deficiency (VAD) are the most important public health issues, and a food-based strategy is crucial to combat those health problems among the vulnerable group of people.\nMethods: Composite biscuits were made with 100:0:0, 90:5:5, 80:10:10, 70:15:15, 60:20:20, and 50:25:25 percent wheat, haricot bean, and orange-fleshed sweet potato (OFSP) flours.Standard methods were used to evaluate the proximate compositions, beta-carotene, physical properties, functional properties, and sensory acceptability. A one-way analysis of variance model was used to statistically evaluate the data using the statistical analysis system software package, version 9.0 standard methods.\nResults: The results showed that partially replacing wheat with haricot beans and OFSP increased the beta-carotene and proximate compositions significantly. When wheat was replaced with haricot beans and OFSP, the physical characteristics of the biscuits did not vary significantly from those of biscuits made entirely of wheat flour. Sensory acceptability (appearance, color, flavor, taste and overall acceptability) was higher in the composite biscuits with up to 40% wheat substitution than in the 100% wheat flour biscuits.\nConclusion: Based on the findings of this report, replacing wheat with OFSP and haricot beans in biscuit formulation appears to be promising in improving nutritional quality, sensory acceptability, and beta carotene. It is proposed that these products can mitigate food insecurity and deficiency of vitamin A.",
                "Chemistry", "Organic Chemistry",
                "https://objectstorage.us-ashburn-1.oraclecloud.com/n/idvpzhveofap/b/research-exchange-pdf-storage-bucket/o/chemistry.pdf",
                null, "https://osf.io/y3anx");
        a7.setScore(33);
        Article savedA7 = articleRepository.save(a7);

        User a7AuthorUser1 = new User("Fieben Kindeya", "fkindey", "fkindey@gmail.com", hashedPassword, salt,
                "User");
        User a7AuthorUser2 = new User("Welday Hailu", "whailu", "whailu@gmail.com", hashedPassword, salt,
                "User");
        User a7AuthorUser3 = new User("Tilku Dessalegn2", "tdessal", "tdessal@gmail.com", hashedPassword, salt,
                "User");
        User a7AuthorUser4 = new User("Gesessew L. Kibr", "gkibr", "gkibr@gmail.com", hashedPassword, salt,
                "User");

        userRepository.saveAll(Arrays.asList(a7AuthorUser1, a7AuthorUser2, a7AuthorUser3, a7AuthorUser4));

        Author a7Author1 = new Author("fkindey", savedA7);
        Author a7Author2 = new Author("whailu", savedA7);
        Author a7Author3 = new Author("tdessal", savedA7);
        Author a7Author4 = new Author("gkibr", savedA7);
        authorRepository.saveAll(Arrays.asList(a7Author1, a7Author2, a7Author3, a7Author4));

        Tag a7Tag1 = new Tag("Malnutrition", savedA7);
        Tag a7Tag2 = new Tag("Deficiency", savedA7);
        tagRepository.saveAll(Arrays.asList(a7Tag1, a7Tag2));

        Link a7Link1 = new Link("https://f1000research.com/articles/10-506", savedA7);
        linkRepository.save(a7Link1);

        User a7UserExpert1 = new User("Warinporn Klunklin", "wklunkl", "wklunkl@gmail.com", hashedPassword,
                salt,
                "Expert");
        User a7UserExpert2 = new User("Elisa Julianti", "ejulian", "ejulian@gmail.com", hashedPassword, salt,
                "Expert");
        User a7UserExpert3 = new User("Mesfin Wogayehu Tenagashaw", "mtenaga", "mtenaga@gmail.com",
                hashedPassword,
                salt,
                "Expert");
        userRepository.saveAll(Arrays.asList(a7UserExpert1, a7UserExpert2, a7UserExpert3));

        Expert a7Expert1 = new Expert("wklunkl", "Chemistry");
        Expert a7Expert2 = new Expert("ejulian", "Chemistry");
        Expert a7Expert3 = new Expert("mtenaga", "Chemistry");
        expertRepository.saveAll(Arrays.asList(a7Expert1, a7Expert2, a7Expert3));

        expertReviewService.postExpertReview(savedA7.getId(),
                new ExpertReviewForm(a7UserExpert1.getUsername(), "review", "Needs Work", null));

        expertReviewService.postExpertReview(savedA7.getId(),
                new ExpertReviewForm(a7UserExpert2.getUsername(), "review", "Approved", null));

        expertReviewService.postExpertReview(savedA7.getId(),
                new ExpertReviewForm(a7UserExpert3.getUsername(), "review", "Needs Work", null));

        // Add article 8
        Article a8 = new Article(
                "Effect of environmental factors on blood counts of Gambusia affinis caught at Brantas River watershed, Indonesia",
                "Background: Protein-energy malnutrition and vitamin A deficiency (VAD) are the most important public health issues, and a food-based strategy is crucial to combat those health problems among the vulnerable group of people.\nMethods: Composite biscuits were made with 100:0:0, 90:5:5, 80:10:10, 70:15:15, 60:20:20, and 50:25:25 percent wheat, haricot bean, and orange-fleshed sweet potato (OFSP) flours.Standard methods were used to evaluate the proximate compositions, beta-carotene, physical properties, functional properties, and sensory acceptability. A one-way analysis of variance model was used to statistically evaluate the data using the statistical analysis system software package, version 9.0 standard methods.\nResults: The results showed that partially replacing wheat with haricot beans and OFSP increased the beta-carotene and proximate compositions significantly. When wheat was replaced with haricot beans and OFSP, the physical characteristics of the biscuits did not vary significantly from those of biscuits made entirely of wheat flour. Sensory acceptability (appearance, color, flavor, taste and overall acceptability) was higher in the composite biscuits with up to 40% wheat substitution than in the 100% wheat flour biscuits.\nConclusion: Based on the findings of this report, replacing wheat with OFSP and haricot beans in biscuit formulation appears to be promising in improving nutritional quality, sensory acceptability, and beta carotene. It is proposed that these products can mitigate food insecurity and deficiency of vitamin A.",
                "Earth Science", "General/Other",
                "https://objectstorage.us-ashburn-1.oraclecloud.com/n/idvpzhveofap/b/research-exchange-pdf-storage-bucket/o/earthscience.pdf",
                null,
                "https://figshare.com/articles/dataset/Water_quality_parameters_and_hematology_profile_of_Gambusia_affinis_caught_at_Brantas_River_watershed_Indonesia/16895227/1");
        a8.setScore(74);
        Article savedA8 = articleRepository.save(a8);

        User a8AuthorUser1 = new User("Asus Maizar Suryanto Hertika", "ahertik", "ahertik@gmail.com",
                hashedPassword,
                salt,
                "User");
        User a8AuthorUser2 = new User("Diana Arfiati", "darfiat", "darfiat@gmail.com", hashedPassword, salt,
                "User");
        User a8AuthorUser3 = new User("Evellin Dewi Lusiana", "elusian", "elusian@gmail.com", hashedPassword,
                salt,
                "User");
        User a8AuthorUser4 = new User("Renanda B.D.S. Putra", "rputra", "rputra@gmail.com", hashedPassword,
                salt,
                "User");

        userRepository.saveAll(Arrays.asList(a8AuthorUser1, a8AuthorUser2, a8AuthorUser3, a8AuthorUser4));

        Author a8Author1 = new Author("ahertik", savedA8);
        Author a8Author2 = new Author("darfiat", savedA8);
        Author a8Author3 = new Author("elusian", savedA8);
        Author a8Author4 = new Author("rputra", savedA8);
        authorRepository.saveAll(Arrays.asList(a8Author1, a8Author2, a8Author3, a8Author4));

        Tag a8Tag1 = new Tag("Water", savedA8);
        Tag a8Tag2 = new Tag("Polution", savedA8);
        Tag a8Tag3 = new Tag("Ecosystem", savedA8);
        tagRepository.saveAll(Arrays.asList(a8Tag1, a8Tag2, a8Tag3));

        Link a8Link1 = new Link("https://f1000research.com/articles/10-1169", savedA8);
        linkRepository.save(a8Link1);

        User a8UserExpert1 = new User("Akhmad Mukti", "amukti", "amukti@gmail.com", hashedPassword, salt,
                "Expert");
        User a8UserExpert2 = new User("Mohammad Tamrin Mohamad Lal", "mlal", "mlal@gmail.com", hashedPassword,
                salt,
                "Expert");
        userRepository.saveAll(Arrays.asList(a8UserExpert1, a8UserExpert2));

        Expert a8Expert1 = new Expert("amukti", "Earth Science");
        Expert a8Expert2 = new Expert("mlal", "Earth Science");
        expertRepository.saveAll(Arrays.asList(a8Expert1, a8Expert2));

        expertReviewService.postExpertReview(savedA8.getId(),
                new ExpertReviewForm(a8UserExpert1.getUsername(), "review", "Approved", null));

        expertReviewService.postExpertReview(savedA8.getId(),
                new ExpertReviewForm(a8UserExpert2.getUsername(), "review", "Approved", null));

        // Add article 9
        Article a9 = new Article(
                "Proactive measures to eradicate Malaysia's poverty in IR4.0 era: a shared prosperity vision",
                "Background: In the country's shared prosperity vision, Malaysia aspires to uplift the bottom 40% household income group (B40) by addressing wealth and income disparities. By 2030, the nation seeks to eradicate poverty through the provision of employment opportunities and career progression plans. A grey area between the nation's aspirations and actions in practice can be observed because the goals have not been achieved despite numerous efforts aimed at the upliftment of the B40 group. The nation is still way behind its targeted outcomes despite various policies being implemented, which could be attributed to the mismatch between government policies and that of organisational practice. Thus, this study explores the rationale of strategic government intervention in managing B40 talent in the IR4.0 era.\nMethods: A general qualitative inquiry method that used 11 semi-structured interviews was carried out with representatives of Malaysia's policy makers', training providers, and trainees. All Interview questions centred around measures, importance and outcomes of B40 youth training from a multi-stakeholder perspective. Data were thematically analysed in five stages using NVivo.\nResults: Training, which includes IR4.0 era digital skills, is the key to uplifting the B40 youth to eradicate poverty. Proactive measures are imperative in the success of B40 youth training towards poverty eradication.\nConclusions: This study contributes to the existing literature and helps practitioners by addressing the current gap in Malaysia's aspirations versus organisational practice. Stakeholders should formulate proactive strategies to ensure that the right trainees are matched with the right training providers and government policies. A linkage between government policies and industry requirements needs to be established as opposed to the present discontinuity. A structured training needs analysis should be applied through a collaboration between industries and governments. Then, B40 individuals commonly found in lower-level positions can be pooled into the career pathway towards a shift into M40.",
                "Economics", "Macroeconomics",
                "https://objectstorage.us-ashburn-1.oraclecloud.com/n/idvpzhveofap/b/research-exchange-pdf-storage-bucket/o/economics.pdf",
                null,
                "https://figshare.com/articles/dataset/Compiled_transcripts_pdf/16384770");
        a9.setScore(3);
        Article savedA9 = articleRepository.save(a9);

        User a9AuthorUser1 = new User("Darshana Darmalinggam", "ddarmal", "ddarmal@gmail.com", hashedPassword,
                salt,
                "User");
        User a9AuthorUser2 = new User("Maniam Kaliannan", "mkalia", "mkalia@gmail.com", hashedPassword, salt,
                "User");
        User a9AuthorUser3 = new User("Magiswary Dorasamy", "mdorasa", "mdorasa@gmail.com", hashedPassword,
                salt,
                "User");
        userRepository.saveAll(Arrays.asList(a9AuthorUser1, a9AuthorUser2, a9AuthorUser3));

        Author a9Author1 = new Author("ddarmal", savedA9);
        Author a9Author2 = new Author("mkalia", savedA9);
        Author a9Author3 = new Author("mdorasa", savedA9);
        authorRepository.saveAll(Arrays.asList(a9Author1, a9Author2, a9Author3));

        Tag a9Tag1 = new Tag("Malaysia", savedA9);
        Tag a9Tag2 = new Tag("Poverty", savedA9);
        Tag a9Tag3 = new Tag("Equality", savedA9);
        tagRepository.saveAll(Arrays.asList(a9Tag1, a9Tag2, a9Tag3));

        Link a9Link1 = new Link(
                "https://figshare.com/articles/dataset/Interview_guide_for_proactive_measures_to_eradicate_poverty_in_Malaysia_in_IR4_0_Era_A_shared_prosperity_vision_/16735426",
                savedA9);
        Link a9Link2 = new Link("https://f1000research.com/articles/10-1094", savedA9);
        linkRepository.saveAll(Arrays.asList(a9Link1, a9Link2));

        User a9UserExpert1 = new User("Lew Tek-Yew", "ltekyew", "ltekyew@gmail.com", hashedPassword, salt,
                "Expert");
        User a9UserExpert2 = new User("Magda Hewitt", "mhewitt", "mhewitt@gmail.com", hashedPassword, salt,
                "Expert");
        userRepository.saveAll(Arrays.asList(a9UserExpert1, a9UserExpert2));

        Expert a9Expert1 = new Expert("ltekyew", "Business");
        Expert a9Expert2 = new Expert("mhewitt", "Economics");
        expertRepository.saveAll(Arrays.asList(a9Expert1, a9Expert2));

        expertReviewService.postExpertReview(savedA9.getId(),
                new ExpertReviewForm(a9UserExpert1.getUsername(), "review", "Approved", null));

        expertReviewService.postExpertReview(savedA9.getId(),
                new ExpertReviewForm(a9UserExpert2.getUsername(), "review", "Approved", null));

        // Add article 10
        Article a10 = new Article(
                "Immersive virtual classroom as an education tool for color barrier-free presentations: a pilot study",
                "Background: This study aimed to propose an experiential approach for understanding color vision variation using virtual reality technology.\nMethods: The study design was adapted from the phase 1 clinical trial for medical apps. A virtual classroom was developed in a three-dimensional space, and ten healthy university students were tested to understand color vision variations.\nResults: No participant interrupted the experience due to VR sickness. Most participants noted that the virtual classroom was an excellent educational tool, which could help teachers understand the problems associated with [visual analog scale (VAS): mean ± standard deviation (SD), 9.6 ± 0.6] and obtain a better understanding of (VAS: mean ± SD, 9.0 ± 1.0) color vision deficiencies.\nConclusions: A pilot study was conducted on the impact of immersive virtual classroom experiences as an educational tool for color barrier-free presentations. This approach may help the participants to respond appropriately to children who suffer from this disorder. It is necessary to evaluate the impact of this approach on new teachers.",
                "Education", "General/Other",
                "https://objectstorage.us-ashburn-1.oraclecloud.com/n/idvpzhveofap/b/research-exchange-pdf-storage-bucket/o/education.pdf",
                null,
                "https://osf.io/3kjvr/");
        a10.setScore(-26);
        Article savedA10 = articleRepository.save(a10);

        User a10AuthorUser1 = new User("Sayaka Fukuyama", "sfukuya", "sfukuya@gmail.com", hashedPassword,
                salt,
                "User");
        User a10AuthorUser2 = new User("Toki Saito", "tsaito", "tsaito@gmail.com", hashedPassword, salt,
                "User");
        User a10AuthorUser3 = new User("Daisuke Ichikawa", "dichika", "dichika@gmail.com", hashedPassword, salt,
                "User");
        User a10AuthorUser4 = new User("Ayako Kohyama", "akohyam", "akohyam@gmail.com", hashedPassword, salt,
                "User");
        User a10AuthorUser5 = new User("Hiroshi Oyama", "hoyama", "hoyama@gmail.com", hashedPassword, salt,
                "User");
        userRepository
                .saveAll(Arrays.asList(a10AuthorUser1, a10AuthorUser2, a10AuthorUser3, a10AuthorUser4,
                        a10AuthorUser5));

        Author a10Author1 = new Author("sfukuya", savedA10);
        Author a10Author2 = new Author("tsaito", savedA10);
        Author a10Author3 = new Author("dichika", savedA10);
        Author a10Author4 = new Author("akohyam", savedA10);
        Author a10Author5 = new Author("hoyama", savedA10);
        authorRepository.saveAll(Arrays.asList(a10Author1, a10Author2, a10Author3, a10Author4, a10Author5));

        Tag a10Tag1 = new Tag("Virtual Reality", savedA10);
        Tag a10Tag2 = new Tag("Color", savedA10);
        tagRepository.saveAll(Arrays.asList(a10Tag1, a10Tag2));

        Link a10Link1 = new Link("https://osf.io/3kjvr/", savedA10);
        Link a10Link2 = new Link("https://f1000research.com/articles/10-985", savedA10);
        linkRepository.saveAll(Arrays.asList(a10Link1, a10Link2));

        User a10UserExpert1 = new User("Alice Skelton", "ashelto", "ashelto@gmail.com", hashedPassword, salt,
                "Expert");
        User a10UserExpert2 = new User("Teresa M. Chan", "tchan", "tchan@gmail.com", hashedPassword, salt,
                "Expert");
        User a10UserExpert3 = new User("Juan Luis Higuera-Trujillo", "jhiguer", "jhiguer@gmail.com",
                hashedPassword,
                salt, "Expert");
        userRepository.saveAll(Arrays.asList(a10UserExpert1, a10UserExpert2, a10UserExpert3));

        Expert a10Expert1 = new Expert("ashelto", "Psychology");
        Expert a10Expert2 = new Expert("tchan", "Education");
        Expert a10Expert3 = new Expert("jhiguer", "Engineering");
        expertRepository.saveAll(Arrays.asList(a10Expert1, a10Expert2, a10Expert3));

        expertReviewService.postExpertReview(savedA10.getId(),
                new ExpertReviewForm(a10UserExpert1.getUsername(), "review", "Rejected", null));

        expertReviewService.postExpertReview(savedA10.getId(),
                new ExpertReviewForm(a10UserExpert2.getUsername(), "review", "Rejected", null));

        expertReviewService.postExpertReview(savedA10.getId(),
                new ExpertReviewForm(a10UserExpert3.getUsername(), "review", "Rejected", null));

        // Add article 11
        Article a11 = new Article(
                "Assembling cheap, high-performance microphones for recording terrestrial wildlife: the Sonitor system",
                "Passive acoustic monitoring of wildlife requires sound recording systems. Several cheap, high-performance, or open-source solutions currently exist for recording soundscapes, but all rely on commercial microphones. Commercial microphones are relatively expensive, specialized for particular taxa, and often have incomplete technical specifications. We designed Sonitor, an open-source microphone system to address all needs of ecologists that sample terrestrial wildlife acoustically. We evaluated the cost and durability of our system and measured trade-offs that are seldom acknowledged but which universally limit microphones' functions: weatherproofing versus sound attenuation, windproofing versus transmission loss after rain, signal loss in long cables, and analog sound amplification versus directivity with acoustic horns. We propose five microphone configurations suiting different budgets (from 8 to 33 EUR per unit), and fulfilling different sound quality and flexibility requirements. The Sonitor system consists of sturdy acoustic sensors that cover the entire sound frequency spectrum of sonant terrestrial wildlife at a fraction of the cost of commercial microphones.",
                "Engineering", "Electrical Engineering",
                "https://objectstorage.us-ashburn-1.oraclecloud.com/n/idvpzhveofap/b/research-exchange-pdf-storage-bucket/o/engineering.pdf",
                null,
                "https://osf.io/hezkw/");
        a11.setScore(126);
        Article savedA11 = articleRepository.save(a11);

        User a11AuthorUser1 = new User("Kevin Darras", "kdarras", "kdarras@gmail.com", hashedPassword,
                salt,
                "User");
        User a11AuthorUser2 = new User("Bjorn Kolbrek", "bkolbre", "bkolbre@gmail.com", hashedPassword, salt,
                "User");
        User a11AuthorUser3 = new User("Andreas Knorr", "aknorr", "aknorr@gmail.com", hashedPassword, salt,
                "User");
        User a11AuthorUser4 = new User("Volker Meyer", "vmeyer", "vmeyer@gmail.com", hashedPassword, salt,
                "User");
        User a11AuthorUser5 = new User("Mike Zippert", "mzipper", "mzipper@gmail.com", hashedPassword, salt,
                "User");
        User a11AuthorUser6 = new User("Arne Wenzel", "awenzel", "awenzel@gmail.com", hashedPassword, salt,
                "User");
        userRepository
                .saveAll(Arrays.asList(a11AuthorUser1, a11AuthorUser2, a11AuthorUser3, a11AuthorUser4,
                        a11AuthorUser5,
                        a11AuthorUser6));

        Author a11Author1 = new Author("kdarras", savedA11);
        Author a11Author2 = new Author("bkolbre", savedA11);
        Author a11Author3 = new Author("aknorr", savedA11);
        Author a11Author4 = new Author("vmeyer", savedA11);
        Author a11Author5 = new Author("mzipper", savedA11);
        Author a11Author6 = new Author("awenzel", savedA11);
        authorRepository.saveAll(
                Arrays.asList(a11Author1, a11Author2, a11Author3, a11Author4, a11Author5, a11Author6));

        Tag a11Tag1 = new Tag("Sound", savedA11);
        Tag a11Tag2 = new Tag("Acoustic", savedA11);
        tagRepository.saveAll(Arrays.asList(a11Tag1, a11Tag2));

        Link a11Link = new Link("https://f1000research.com/articles/7-1984", savedA11);
        linkRepository.save(a11Link);

        User a11UserExpert1 = new User("Sarab S. Sethi", "ssethi", "ssethi@gmail.com", hashedPassword, salt,
                "Expert");
        User a11UserExpert2 = new User("Holger Klinck", "hklinck", "hklinck@gmail.com", hashedPassword, salt,
                "Expert");
        User a11UserExpert3 = new User("Catharina Karlsson", "ckarlss", "ckarlss@gmail.com", hashedPassword,
                salt, "Expert");
        userRepository.saveAll(Arrays.asList(a11UserExpert1, a11UserExpert2, a11UserExpert3));

        Expert a11Expert1 = new Expert("ssethi", "Engineering");
        Expert a11Expert2 = new Expert("hklinck", "Engineering");
        Expert a11Expert3 = new Expert("ckarlss", "Engineering");
        expertRepository.saveAll(Arrays.asList(a11Expert1, a11Expert2, a11Expert3));

        expertReviewService.postExpertReview(savedA11.getId(),
                new ExpertReviewForm(a11UserExpert1.getUsername(), "review", "Approved", null));
        expertReviewService.postExpertReview(savedA11.getId(),
                new ExpertReviewForm(a11UserExpert2.getUsername(), "review", "Approved", null));
        expertReviewService.postExpertReview(savedA11.getId(),
                new ExpertReviewForm(a11UserExpert3.getUsername(), "review", "Approved", null));

        // Add article 12
        Article a12 = new Article(
                "The role of newspapers published in North Sumatra during Indonesia's independence struggle between 1916-1925: A discourse analysis",
                "Background: In the history of Indonesian independence, newspapers not only functioned as a medium of information but also as a tool for the struggle for independence. This is because of the role of newspapers in bringing out the spirit of nationalism and the concept of \"nation\" to the Indonesian people through various writings and reports in each edition. This study aims to describe the form of Indonesia's struggle for independence in newspapers published in North Sumatra in 1916-1925.\nMethods: This research uses the critical discourse analysis method. The research data are in the form of texts published in thirteen indigenous newspapers published in North Sumatra in 1916-1925. Data analysis used three structures of Teun A. van Dijk's discourse analysis model, namely: macrostructure, superstructure, and microstructure.\nResults: From 1910-1925 there were 24 indigenous newspapers published in North Sumatra. Of these, there were 13 newspapers demanding Indonesian independence: Soeara Djawa, Pewarta Deli, Benih Merdeka, Perempoean Bergerak, Soeara Bondjol, Sinar Zaman, Orgaan Bataksche Studiefonds, Andalas, Mandailing, Warta Timur, Al Moektabas, Tjermin Karo, Soeara Batak. These newspapers published articles that fought for Indonesian independence by demanding Indonesian independence openly, criticizing various policies of the Dutch Colonial Government, and building awareness of Indonesian nationalism.\nConclusion: The role of newspapers in the struggle for Indonesian independence in North Sumatra in 1916-1925 can be seen from the findings of 51 articles demanding Indonesia's independence from Dutch colonialism, criticizing the policies of the Dutch colonial government, and building the spirit of nationalism to encourage the Indonesian people to fight for their independence.",
                "History", "General/Other",
                "https://objectstorage.us-ashburn-1.oraclecloud.com/n/idvpzhveofap/b/research-exchange-pdf-storage-bucket/o/history.pdf",
                null,
                "https://figshare.com/articles/dataset/The_results_of_the_analysis_of_macrostructure_superstructure_and_microstructure_of_Indigenous_newspapers_published_in_North_Sumatra_1916-1925_/17894687");
        a12.setScore(0);
        Article savedA12 = articleRepository.save(a12);

        User a12AuthorUser1 = new User("Ichwan Azhari", "iazhari", "iazhari@gmail.com", hashedPassword, salt,
                "User");
        User a12AuthorUser2 = new User("Ricu Sidiq", "rsidiq", "rsidiq@gmail.com", hashedPassword, salt,
                "User");
        User a12AuthorUser3 = new User("Ika Purnamasari", "ipurnam", "ipurnam@gmail.com", hashedPassword, salt,
                "User");
        userRepository.saveAll(Arrays.asList(a12AuthorUser1, a12AuthorUser2, a12AuthorUser3));

        Author a12Author1 = new Author("iazhari", savedA12);
        Author a12Author2 = new Author("rsidiq", savedA12);
        Author a12Author3 = new Author("ipurnam", savedA12);
        authorRepository.saveAll(Arrays.asList(a12Author1, a12Author2, a12Author3));

        Tag a12Tag1 = new Tag("North Sumatra", savedA11);
        Tag a12Tag2 = new Tag("Newspapers", savedA11);
        Tag a12Tag3 = new Tag("Independence", savedA11);
        Tag a12Tag4 = new Tag("Nationality", savedA11);
        tagRepository.saveAll(Arrays.asList(a12Tag1, a12Tag2, a12Tag3, a12Tag4));

        Link a12Link1 = new Link("https://www.perpusnas.go.id/", savedA12);
        Link a12Link2 = new Link("https://f1000research.com/articles/11-249", savedA12);
        linkRepository.saveAll(Arrays.asList(a12Link1, a12Link2));

        // Add article 13
        Article a13 = new Article(
                "Acedia, loneliness, and the mandatory celibacy of Catholic parish clergy: a theological-sociological exploratory analysis",
                "This article utilizes the analytical concept of acedia as the fundamental theoretical framework and applies a systematic literature review of peer-reviewed materials and documents on spiritual sloth, spiritual dryness, Catholic clerical celibacy, social bonding and communal spirituality. This article explores how the Catholic parish clergy's mandatory celibacy intensifies loneliness and facilitates the spiritual sloth of parish clergy or what is theologically known as acedia. Unlike religious priests who live in religious communities, parish clerics fundamentally live, work, and pray alone in the parish, without strong communal support from fellow priests, bishops, and lay parishioners; thus, making them prone to loneliness, a main component of acedia. This article argues that mandatory celibacy further deprives parish clerics of the social and spiritual support necessary to enhance diocesan clerical spirituality and strengthen spiritual resistance against acedia. It recommends a structural adjustment in the social and spiritual life of parish priests, creating small communities of priests situated in similar territory or districts to allow them to live and work as a team with strong social and spiritual support in the spirit of \"living baptismally\" to overcome priestly acedia.",
                "Philosophy", "General/Other",
                "https://objectstorage.us-ashburn-1.oraclecloud.com/n/idvpzhveofap/b/research-exchange-pdf-storage-bucket/o/philosophy.pdf",
                null, null);
        a13.setScore(2);
        Article savedA13 = articleRepository.save(a13);

        User a13AuthorUser = new User("Vivencio Ballano", "vballan", "vballan@gmail.com", hashedPassword, salt,
                "User");
        userRepository.save(a13AuthorUser);

        Author a13Author = new Author("vballan", savedA13);
        authorRepository.save(a13Author);

        Tag a13Tag1 = new Tag("Celibacy", savedA13);
        Tag a13Tag2 = new Tag("Spirituality", savedA13);
        Tag a13Tag3 = new Tag("Loneliness", savedA13);
        Tag a13Tag4 = new Tag("Bonding", savedA13);
        tagRepository.saveAll(Arrays.asList(a13Tag1, a13Tag2, a13Tag3, a13Tag4));

        Link a13Link = new Link("https://f1000research.com/articles/10-1195", savedA13);
        linkRepository.save(a13Link);

        User a13UserExpert1 = new User("Edward Vacek", "evacek", "evacek@gmail.com", hashedPassword, salt,
                "Expert");
        User a13UserExpert2 = new User("Mark Regnerus", "mregner", "mregner@gmail.com", hashedPassword, salt,
                "Expert");
        User a13UserExpert3 = new User("Lluis Oviedo", "loviedo", "loviedo@gmail.com", hashedPassword,
                salt, "Expert");
        userRepository.saveAll(Arrays.asList(a13UserExpert1, a13UserExpert2, a13UserExpert3));

        Expert a13Expert1 = new Expert("evacek", "Philosophy");
        Expert a13Expert2 = new Expert("mregner", "Philosophy");
        Expert a13Expert3 = new Expert("loviedo", "Philosophy");
        expertRepository.saveAll(Arrays.asList(a13Expert1, a13Expert2, a13Expert3));

        expertReviewService.postExpertReview(savedA13.getId(),
                new ExpertReviewForm(a13UserExpert1.getUsername(), "review", "Approved", null));
        expertReviewService.postExpertReview(savedA13.getId(),
                new ExpertReviewForm(a13UserExpert2.getUsername(), "review", "Rejected", null));
        expertReviewService.postExpertReview(savedA13.getId(),
                new ExpertReviewForm(a13UserExpert3.getUsername(), "review", "Needs Work", null));

        // Add article 14
        Article a14 = new Article(
                "Elections in Nondemocratic Settings: When and Why Do They Help Regime Survival?",
                "This article utilizes the analytical concept of acedia as the fundamental theoretical framework and applies a systematic literature review of peer-reviewed materials and documents on spiritual sloth, spiritual dryness, Catholic clerical celibacy, social bonding and communal spirituality. This article explores how the Catholic parish clergy's mandatory celibacy intensifies loneliness and facilitates the spiritual sloth of parish clergy or what is theologically known as acedia. Unlike religious priests who live in religious communities, parish clerics fundamentally live, work, and pray alone in the parish, without strong communal support from fellow priests, bishops, and lay parishioners; thus, making them prone to loneliness, a main component of acedia. This article argues that mandatory celibacy further deprives parish clerics of the social and spiritual support necessary to enhance diocesan clerical spirituality and strengthen spiritual resistance against acedia. It recommends a structural adjustment in the social and spiritual life of parish priests, creating small communities of priests situated in similar territory or districts to allow them to live and work as a team with strong social and spiritual support in the spirit of \"living baptismally\" to overcome priestly acedia.",
                "Political Science", "General/Other",
                "https://objectstorage.us-ashburn-1.oraclecloud.com/n/idvpzhveofap/b/research-exchange-pdf-storage-bucket/o/politicalscience.pdf",
                null, "https://data.mendeley.com/datasets/p9wb86zyk2/2");
        a14.setScore(2);
        Article savedA14 = articleRepository.save(a14);

        User a14AuthorUser = new User("Katsunori Seki", "kseki", "kseki@gmail.com", hashedPassword, salt,
                "User");
        userRepository.save(a14AuthorUser);

        Author a14Author = new Author("kseki", savedA14);
        authorRepository.save(a14Author);

        Tag a14Tag1 = new Tag("Elections", savedA14);
        Tag a14Tag2 = new Tag("Regime", savedA14);
        Tag a14Tag3 = new Tag("Boycott", savedA14);
        tagRepository.saveAll(Arrays.asList(a14Tag1, a14Tag2, a14Tag3));

        Link a14Link = new Link("https://f1000research.com/articles/11-273", savedA14);
        linkRepository.save(a14Link);

        User a14UserExpert = new User("Nam Kyu Kim", "nkim", "nkim@gmail.com", hashedPassword, salt, "Expert");
        userRepository.save(a14UserExpert);

        Expert a14Expert = new Expert("nkim", "Political Science");
        expertRepository.save(a14Expert);

        expertReviewService.postExpertReview(savedA14.getId(),
                new ExpertReviewForm(a14UserExpert.getUsername(), "review", "Needs Work", null));

        // Add article 15
        Article a15 = new Article(
                "Perceived stress and generalized anxiety in the Indian population due to lockdown during the COVID-19 pandemic: a cross-sectional study",
                "Background: Research on the psychosocial toll of the COVID-19 pandemic is being conducted in various countries. This study aimed to examine stress levels and causal stressors for perceived stress and generalized anxiety in the Indian population related to the lockdown during the COVID-19 pandemic.\nMethods: A total of 300 adults were invited to participate in the online study via snowball and virtual snowball sampling. They were requested to complete electronic survey forms for assessing perceived stress and anxiety, and questions related to psychosocial stressors. Frequency and percentage were used for categorical variables. Unpaired t-test was applied to compare responses based on gender, level of education, employment, and place of residence. A p-value of <0.05 was considered statistically significant.\nResult: In total, 257 out of the 300 invited, responded and completed the survey. Men accounted for 58% (n=149) of the respondents. Overall, 84% (n=217) of participants had moderate to severe levels of perceived stress and 88% (n=228) had moderate to severe levels of anxiety. Women, as well as those not employed, reported significantly higher perceived stress and anxiety, urban residents reported higher perceived stress, while level of education had no difference in terms of perceived stress as well as anxiety. Fear of contracting COVID-19 was the highest stressor followed by difficulties in executing a routine exercise schedule and worry about the future.\nConclusion: The psychosocial impact of the nationwide lockdown on the Indian population has been high. Vulnerable groups for increased stress and anxiety include women, younger ages, and the unemployed. The stressors recognized include fear of contracting COVID-19, inability to execute a routine exercise schedule and worry about the future.",
                "Psychology", "General/Other",
                "https://objectstorage.us-ashburn-1.oraclecloud.com/n/idvpzhveofap/b/research-exchange-pdf-storage-bucket/o/psychology.pdf",
                null,
                "https://figshare.com/articles/dataset/Raw_data_PSS_GAD_Psychosocial_impact_of_lockdown_csv/12860060/2");
        a15.setScore(83);
        Article savedA15 = articleRepository.save(a15);

        User a15AuthorUser1 = new User("Naina Wakode", "nwakode", "nwakode@gmail.com", hashedPassword, salt,
                "User");
        User a15AuthorUser2 = new User("Santosh Wakode", "swakode", "swakode@gmail.com", hashedPassword, salt,
                "User");
        User a15AuthorUser3 = new User("John Santoshi", "jsantos", "jsantos@gmail.com", hashedPassword, salt,
                "User");
        userRepository.saveAll(Arrays.asList(a15AuthorUser1, a15AuthorUser2, a15AuthorUser3));

        Author a15Author1 = new Author("nwakode", savedA15);
        Author a15Author2 = new Author("swakode", savedA15);
        Author a15Author3 = new Author("jsantos", savedA15);
        authorRepository.saveAll(Arrays.asList(a15Author1, a15Author2, a15Author3));

        Tag a15Tag1 = new Tag("India", savedA15);
        Tag a15Tag2 = new Tag("Stress", savedA15);
        Tag a15Tag3 = new Tag("Anxiety", savedA15);
        Tag a15Tag4 = new Tag("COVID-19", savedA15);
        tagRepository.saveAll(Arrays.asList(a15Tag1, a15Tag2, a15Tag3, a15Tag4));

        Link a15Link = new Link("https://f1000research.com/articles/9-1233", savedA15);
        linkRepository.save(a15Link);

        User a15UserExpert1 = new User("Krishna Prasad Muliyala", "kmuliya", "kmuliya@gmail.com",
                hashedPassword, salt,
                "Expert");
        User a15UserExpert2 = new User("Javier Santabarbara", "jsantab", "jsantab@gmail.com", hashedPassword,
                salt,
                "Expert");
        User a15UserExpert3 = new User("Kiran Chaudhari", "kchaudh", "kchaudh@gmail.com", hashedPassword,
                salt, "Expert");
        userRepository.saveAll(Arrays.asList(a15UserExpert1, a15UserExpert2, a15UserExpert3));

        Expert a15Expert1 = new Expert("kmuliya", "Psychology");
        Expert a15Expert2 = new Expert("jsantab", "Psychology");
        Expert a15Expert3 = new Expert("kchaudh", "Psychology");
        expertRepository.saveAll(Arrays.asList(a15Expert1, a15Expert2, a15Expert3));

        expertReviewService.postExpertReview(savedA15.getId(),
                new ExpertReviewForm(a15UserExpert1.getUsername(), "review", "Approved", null));
        expertReviewService.postExpertReview(savedA15.getId(),
                new ExpertReviewForm(a15UserExpert2.getUsername(), "review", "Approved", null));
        expertReviewService.postExpertReview(savedA15.getId(),
                new ExpertReviewForm(a15UserExpert3.getUsername(), "review", "Approved", null));
    }

    private String hashPassword(String password, byte[] salt) {
        SecretKeyFactory factory = null;
        try {
            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        byte[] hash = null;
        try {
            hash = factory.generateSecret(spec).getEncoded();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        String hashString = Hex.encodeHexString(hash);
        return hashString;
    }

}
