const axios = require('axios');

async function testFrontendAPI() {
  try {
    console.log('Testing frontend API calls...');
    
    // Step 1: Login
    console.log('\n1. Testing login...');
    const loginResponse = await axios.post('http://localhost:8080/api/auth/login', {
      email: 'admin@insurance.com',
      password: 'admin123'
    }, {
      headers: {
        'Content-Type': 'application/json'
      }
    });
    
    console.log('✅ Login successful');
    console.log('Token:', loginResponse.data.token.substring(0, 50) + '...');
    
    const token = loginResponse.data.token;
    
    // Step 2: Test policies endpoint
    console.log('\n2. Testing policies endpoint...');
    const policiesResponse = await axios.get('http://localhost:8080/api/policies', {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });
    
    console.log('✅ Policies endpoint successful');
    console.log('Number of policies:', policiesResponse.data.length);
    console.log('First policy:', JSON.stringify(policiesResponse.data[0], null, 2));
    
    // Step 3: Test clients endpoint
    console.log('\n3. Testing clients endpoint...');
    const clientsResponse = await axios.get('http://localhost:8080/api/clients', {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });
    
    console.log('✅ Clients endpoint successful');
    console.log('Number of clients:', clientsResponse.data.length);
    
    // Step 4: Test vehicles endpoint
    console.log('\n4. Testing vehicles endpoint...');
    const vehiclesResponse = await axios.get('http://localhost:8080/api/vehicles', {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });
    
    console.log('✅ Vehicles endpoint successful');
    console.log('Number of vehicles:', vehiclesResponse.data.length);
    
    console.log('\n🎉 All API endpoints are working correctly!');
    
  } catch (error) {
    console.error('❌ Error:', error.response?.data || error.message);
    console.error('Status:', error.response?.status);
    console.error('Headers:', error.response?.headers);
  }
}

testFrontendAPI();