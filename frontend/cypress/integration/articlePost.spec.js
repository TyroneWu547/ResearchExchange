describe('View Article Post', () => {
    it('should view an article\'s post from the \'View Articles\' page', () => {
        const title = "Research Paper";
        const postedDate = "12/12/2020";
        const abstract = "Lorem Ipsum is simply dummy text of the printing and typesetting industry.";
        const field = "Computer Science";
        const subField = "Programming Languages";
        const tags = ["JavaScript", "TypeScript", "Compiler"];
        const score = "103";

        cy.visit('/articles');

        cy.get('[class^=ArticleRow_article]').eq(0).within(() => {
            cy.get('[class^=ArticleRow_titleSection]').within(() => {
                cy.get('[class^=ArticleRow_articleTitle]').should('contain', title);
                cy.contains(postedDate);
            });

            cy.get('[class^=ArticleRow_titleSection]').parent().children().eq(1).within(() => {
                cy.get('[class^=ArticleRow_articleAbstract]').should('contain', abstract);
                cy.get('[class^=ArticleRow_subject]').should('contain', field);
                cy.get('[class^=ArticleRow_subject]').should('contain', subField);
            });

            cy.get('[class^=ArticleRow_scoreAndReviews]').within(() => {
                cy.contains(score);
            });

            cy.get('[class^=ArticleRow_tags]').within(() => {
                for (var i = 0; i < 3; i++) {
                    cy.contains(tags[i]);
                }
            });

            cy.get('[class^=ArticleRow_articleTitle]').click();
        });

        cy.url().should('include', '/articles/1');
    });

    it('should view an article\'s post by its url', () => {
        const title = "Research Paper";
        const postedDate = "12/12/2020";
        const abstract = "Lorem Ipsum is simply dummy text of the printing and typesetting industry.";
        const field = "Computer Science";
        const subField = "Programming Languages";
        const tags = ["JavaScript", "TypeScript", "Compiler"];
        const score = "103";

        cy.visit('/articles/1');

        cy.get('[class^=Article_article] [class^=Vote_uncompressedScore]').within(() => {
            cy.contains(score);
        });

        cy.get('[class^=Article_articleInfo]').within(() => {
            cy.get('[class^=Article_articleTitle]').contains(title);
            cy.get('[class^=Article_subject]').contains(field);
            cy.get('[class^=Article_subject]').contains(subField);

            cy.get('[class^=Article_subject]').parent().children().eq(2).within(() => {
                cy.contains(abstract);
            });

            cy.get('[class^=Article_subject]').parent().children().eq(4).within(() => {
                cy.contains(postedDate);
            });

            cy.get('[class^=Article_tags]').within(() => {
                for (var i = 0; i < 3; i++) {
                    cy.contains(tags[i]);
                }
            });
        });
    });
});

describe(() => {
    before(() => {
        cy.visit('/login');

        cy.intercept('POST', '/login', { statusCode: 200, body: { "access_token": "abc.abc.abc" } }).as('login');

        cy.get('input[name="username"]').type("username");
        cy.get('input[name="password"]').type("password");

        cy.get('input[type="submit"]').click();

        cy.wait('@login')
            .its('request.body')
            .should('deep.equal', { username: "username", password: "password" });
    });

    it.skip('should create an article\'s post', () => {
        cy.visit('/articles');

        cy.visit('/post-article');

        cy.intercept('POST', '/articles', { statusCode: 200, body: { "access_token": "abc.abc.abc" } }).as('postArticle');

        const title = "A Research Article";
        const authors = ["Me", "Myself", "I"];
        const abstract = "This paper researches some important research topics";
        const field = "Computer Science";
        const subField = "General/Other";
        const tags = ["Amazingness", "Awesomeness"];
        const repoUrl = "https://github.com";
        const dataUrl = "https://drive.google.com";
        const extraLinks = ["https://www.google.com"];

        cy.fixture('sample.pdf').then(fileContent => {
            cy.get('input[type="file"]').attachFile({
                fileContent: fileContent.toString(),
                fileName: 'sample.pdf',
                mimeType: 'application/pdf'
            });
        });

        cy.get('input[name="title"]').type(title);

        for (const author of authors) {
            cy.get('#authors-container input[type="text"]').type(author);
            cy.get('#authors-container button').click();
        }

        cy.get('textarea[name="abstract"]').type(abstract);
        cy.get('select[name="subject"]').select(field);
        cy.get('select[name="subsubject"]').should('exist');
        cy.get('select[name="subsubject"]').select(subField);

        for (const tag of tags) {
            cy.get('#tags-container input[type="text"]').type(tag);
            cy.get('#tags-container button').click();
        }

        cy.get('input[name="repoUrl"]').type(repoUrl);
        cy.get('input[name="dataUrl"]').type(dataUrl);

        for (const extraLink of extraLinks) {
            cy.get('#extra-links-container input[type="text"]').type(extraLink);
            cy.get('#extra-links-container button').click();
        }

        cy.get('input[type="submit"]').click();

        cy.wait('@postArticle')
            .its('request.body')
            .should('deep.equal', {
                title, authors, abstract, subject, subsubject, tags, repoUrl, dataUrl, extraLinks
            });
    });
});