const request = require('supertest');
const app = require('../server');

describe('REST API Tests', () => {
  describe('GET /api/users', () => {
    it('should return all users', async () => {
      const res = await request(app).get('/api/users');
      expect(res.statusCode).toBe(200);
      expect(Array.isArray(res.body)).toBeTruthy();
    });
  });

  describe('POST /api/users', () => {
    it('should create a new user', async () => {
      const newUser = {
        name: 'Test User',
        email: 'test@example.com'
      };
      const res = await request(app)
        .post('/api/users')
        .send(newUser);
      expect(res.statusCode).toBe(201);
      expect(res.body).toHaveProperty('id');
      expect(res.body.name).toBe(newUser.name);
    });
  });

  describe('GET /api/users/:id', () => {
    it('should return a specific user', async () => {
      // D'abord créer un utilisateur
      const newUser = {
        name: 'John Doe',
        email: 'john@example.com'
      };
      const createRes = await request(app)
        .post('/api/users')
        .send(newUser);
      
      const userId = createRes.body.id;

      // Puis récupérer cet utilisateur
      const res = await request(app).get(`/api/users/${userId}`);
      expect(res.statusCode).toBe(200);
      expect(res.body).toHaveProperty('id');
      expect(res.body.name).toBe('John Doe');
    });
  });
});