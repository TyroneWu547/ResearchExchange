describe('Authentication', () => {
  it('should log in', () => {
    cy.visit('/login');

    cy.intercept('POST', '/login', { statusCode: 200, body: { "access_token": "abc.abc.abc" } }).as('login');

    cy.get('input[name="username"]').type("username");
    cy.get('input[name="password"]').type("password");

    cy.get('input[type="submit"]').click();

    cy.wait('@login')
      .its('request.body')
      .should('deep.equal', { username: "username", password: "password" });

    cy.get('input[type="submit"]').click();
  });

  it('should sign up', () => {
    cy.visit('/signup');

    cy.intercept('POST', '/signup', { statusCode: 200 }).as('signup');

    cy.get('input[name="name"]').type("Jane Doe");
    cy.get('input[name="email"]').type("user@gmail.com");
    cy.get('input[name="username"]').type("user");
    cy.get('input[name="password"]').type("password");
    cy.get('input[name="confirmPassword"]').type("password");

    cy.get('input[type="submit"]').click();

    cy.wait('@signup')
      .its('request.body')
      .should('deep.equal', {
        name: "Jane Doe", email: "user@gmail.com", username: "user",
        password: "password", confirmPassword: "password"
      });
  });
});