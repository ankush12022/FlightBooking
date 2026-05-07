import { useEffect, useMemo, useState } from "react";

const BASE_URL = "http://localhost:6996";

const emptyRegister = {
  name: "",
  email: "",
  password: "",
  role: "USER"
};

const emptyLogin = {
  email: "",
  password: ""
};

const emptyFlight = {
  flightNumber: "",
  source: "",
  destination: "",
  departureTime: "",
  arrivalTime: "",
  totalSeats: "",
  availableSeats: "",
  baseFare: ""
};

const emptySearch = {
  source: "",
  destination: "",
  date: ""
};

const emptyBooking = {
  userId: "",
  flightId: "",
  numberOfTickets: 1,
  passengers: [
    {
      name: "",
      age: "",
      seatNumber: ""
    }
  ]
};

function App() {
  const [token, setToken] = useState(localStorage.getItem("token") || "");
  const [role, setRole] = useState(localStorage.getItem("role") || "");
  const [message, setMessage] = useState("");
  const [registerData, setRegisterData] = useState(emptyRegister);
  const [loginData, setLoginData] = useState(emptyLogin);
  const [flightData, setFlightData] = useState(emptyFlight);
  const [searchData, setSearchData] = useState(emptySearch);
  const [bookingData, setBookingData] = useState(emptyBooking);
  const [flights, setFlights] = useState([]);
  const [loading, setLoading] = useState(false);

  const authHeaders = useMemo(() => {
    return {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`
    };
  }, [token]);

  const showMessage = (text) => {
    setMessage(text);
    setTimeout(() => setMessage(""), 5000);
  };

  const parseResponse = async (response) => {
    const contentType = response.headers.get("content-type") || "";

    if (contentType.includes("application/json")) {
      return response.json();
    }

    return response.text();
  };

  const registerUser = async (event) => {
    event.preventDefault();
    setLoading(true);

    try {
      const response = await fetch(`${BASE_URL}/api/auth/register`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(registerData)
      });

      const data = await parseResponse(response);

      if (response.ok) {
        showMessage(typeof data === "string" ? data : "Registration successful");
        setRegisterData(emptyRegister);
      } else {
        showMessage(data.message || "Registration failed");
      }
    } catch (error) {
      showMessage("Backend is not reachable");
    } finally {
      setLoading(false);
    }
  };

  const loginUser = async (event) => {
    event.preventDefault();
    setLoading(true);

    try {
      const response = await fetch(`${BASE_URL}/api/auth/login`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(loginData)
      });

      const data = await parseResponse(response);

      if (response.ok) {
        localStorage.setItem("token", data.token);
        localStorage.setItem("role", data.role);

        setToken(data.token);
        setRole(data.role);
        setLoginData(emptyLogin);
        showMessage("Login successful");
      } else {
        showMessage(data.message || "Invalid email or password");
      }
    } catch (error) {
      showMessage("Backend is not reachable");
    } finally {
      setLoading(false);
    }
  };

  const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("role");
    setToken("");
    setRole("");
    setFlights([]);
    showMessage("Logged out successfully");
  };

  const addFlight = async (event) => {
    event.preventDefault();
    setLoading(true);

    try {
      const response = await fetch(`${BASE_URL}/api/flights`, {
        method: "POST",
        headers: authHeaders,
        body: JSON.stringify({
          ...flightData,
          totalSeats: Number(flightData.totalSeats),
          availableSeats: Number(flightData.availableSeats),
          baseFare: Number(flightData.baseFare)
        })
      });

      const data = await parseResponse(response);

      if (response.ok) {
        showMessage("Flight added successfully");
        setFlightData(emptyFlight);
        await getAllFlights();
      } else {
        showMessage(data.message || "Failed to add flight");
      }
    } catch (error) {
      showMessage("Backend is not reachable");
    } finally {
      setLoading(false);
    }
  };

  const getAllFlights = async () => {
    if (!token) {
      showMessage("Please login first");
      return;
    }

    setLoading(true);

    try {
      const response = await fetch(`${BASE_URL}/api/flights?page=0&size=20`, {
        method: "GET",
        headers: authHeaders
      });

      const data = await parseResponse(response);

      if (response.ok) {
        setFlights(Array.isArray(data) ? data : data.content || []);
        showMessage("Flights loaded");
      } else {
        showMessage(data.message || "Only ADMIN may access all flights. Try search.");
      }
    } catch (error) {
      showMessage("Backend is not reachable");
    } finally {
      setLoading(false);
    }
  };

  const searchFlights = async (event) => {
    event.preventDefault();
    setLoading(true);

    try {
      const response = await fetch(`${BASE_URL}/api/flights/search?page=0&size=20`, {
        method: "POST",
        headers: authHeaders,
        body: JSON.stringify(searchData)
      });

      const data = await parseResponse(response);

      if (response.ok) {
        setFlights(Array.isArray(data) ? data : data.content || []);
        showMessage("Search completed");
      } else {
        showMessage(data.message || "Search failed");
      }
    } catch (error) {
      showMessage("Backend is not reachable");
    } finally {
      setLoading(false);
    }
  };

  const updateNumberOfTickets = (value) => {
    const count = Number(value);

    if (count < 1) {
      return;
    }

    const updatedPassengers = Array.from({ length: count }, (_, index) => {
      return bookingData.passengers[index] || {
        name: "",
        age: "",
        seatNumber: ""
      };
    });

    setBookingData({
      ...bookingData,
      numberOfTickets: count,
      passengers: updatedPassengers
    });
  };

  const updatePassenger = (index, field, value) => {
    const updatedPassengers = [...bookingData.passengers];

    updatedPassengers[index] = {
      ...updatedPassengers[index],
      [field]: value
    };

    setBookingData({
      ...bookingData,
      passengers: updatedPassengers
    });
  };

  const bookFlight = async (event) => {
    event.preventDefault();
    setLoading(true);

    try {
      const response = await fetch(`${BASE_URL}/api/bookings`, {
        method: "POST",
        headers: authHeaders,
        body: JSON.stringify({
          userId: Number(bookingData.userId),
          flightId: Number(bookingData.flightId),
          numberOfTickets: Number(bookingData.numberOfTickets),
          passengers: bookingData.passengers.map((passenger) => ({
            name: passenger.name,
            age: Number(passenger.age),
            seatNumber: passenger.seatNumber
          }))
        })
      });

      const data = await parseResponse(response);

      if (response.ok) {
        showMessage("Booking successful. Seats updated.");
        setBookingData(emptyBooking);
        await searchAfterBooking();
      } else {
        showMessage(data.message || "Booking failed");
      }
    } catch (error) {
      showMessage("Backend is not reachable");
    } finally {
      setLoading(false);
    }
  };

  const searchAfterBooking = async () => {
    if (searchData.source && searchData.destination && searchData.date) {
      const response = await fetch(`${BASE_URL}/api/flights/search?page=0&size=20`, {
        method: "POST",
        headers: authHeaders,
        body: JSON.stringify(searchData)
      });

      const data = await parseResponse(response);

      if (response.ok) {
        setFlights(Array.isArray(data) ? data : data.content || []);
      }
    } else if (role === "ADMIN") {
      await getAllFlights();
    }
  };

  const fillBookingFlight = (flightId) => {
    setBookingData((oldData) => ({
      ...oldData,
      flightId: String(flightId)
    }));

    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  useEffect(() => {
    if (token && role === "ADMIN") {
      getAllFlights();
    }
  }, [token, role]);

  return (
    <div className="app">
      <header className="hero">
        <div>
          <p className="badge">Spring Boot + React</p>
          <h1>Flight Booking Management System</h1>
          <p className="subtitle">
            Register, login, search flights, add flights, and book tickets with live available-seat updates.
          </p>
        </div>

        {token && (
          <div className="session-card">
            <span>Logged in as</span>
            <strong>{role}</strong>
            <button onClick={logout}>Logout</button>
          </div>
        )}
      </header>

      {message && <div className="message">{message}</div>}
      {loading && <div className="loading">Loading...</div>}

      {!token && (
        <section className="grid two">
          <div className="card">
            <h2>Register</h2>

            <form onSubmit={registerUser}>
              <input
                type="text"
                placeholder="Name"
                value={registerData.name}
                onChange={(event) =>
                  setRegisterData({ ...registerData, name: event.target.value })
                }
                required
              />

              <input
                type="email"
                placeholder="Email"
                value={registerData.email}
                onChange={(event) =>
                  setRegisterData({ ...registerData, email: event.target.value })
                }
                required
              />

              <input
                type="password"
                placeholder="Password"
                value={registerData.password}
                onChange={(event) =>
                  setRegisterData({ ...registerData, password: event.target.value })
                }
                required
              />

              <select
                value={registerData.role}
                onChange={(event) =>
                  setRegisterData({ ...registerData, role: event.target.value })
                }
              >
                <option value="USER">USER</option>
                <option value="ADMIN">ADMIN</option>
              </select>

              <button type="submit">Register</button>
            </form>
          </div>

          <div className="card">
            <h2>Login</h2>

            <form onSubmit={loginUser}>
              <input
                type="email"
                placeholder="Email"
                value={loginData.email}
                onChange={(event) =>
                  setLoginData({ ...loginData, email: event.target.value })
                }
                required
              />

              <input
                type="password"
                placeholder="Password"
                value={loginData.password}
                onChange={(event) =>
                  setLoginData({ ...loginData, password: event.target.value })
                }
                required
              />

              <button type="submit">Login</button>
            </form>
          </div>
        </section>
      )}

      {token && (
        <>
          {role === "ADMIN" && (
            <section className="card">
              <h2>Add Flight</h2>

              <form onSubmit={addFlight} className="grid three">
                <input
                  type="text"
                  placeholder="Flight Number"
                  value={flightData.flightNumber}
                  onChange={(event) =>
                    setFlightData({ ...flightData, flightNumber: event.target.value })
                  }
                  required
                />

                <input
                  type="text"
                  placeholder="Source"
                  value={flightData.source}
                  onChange={(event) =>
                    setFlightData({ ...flightData, source: event.target.value })
                  }
                  required
                />

                <input
                  type="text"
                  placeholder="Destination"
                  value={flightData.destination}
                  onChange={(event) =>
                    setFlightData({ ...flightData, destination: event.target.value })
                  }
                  required
                />

                <input
                  type="datetime-local"
                  value={flightData.departureTime}
                  onChange={(event) =>
                    setFlightData({ ...flightData, departureTime: event.target.value })
                  }
                  required
                />

                <input
                  type="datetime-local"
                  value={flightData.arrivalTime}
                  onChange={(event) =>
                    setFlightData({ ...flightData, arrivalTime: event.target.value })
                  }
                  required
                />

                <input
                  type="number"
                  placeholder="Total Seats"
                  value={flightData.totalSeats}
                  onChange={(event) =>
                    setFlightData({ ...flightData, totalSeats: event.target.value })
                  }
                  required
                />

                <input
                  type="number"
                  placeholder="Available Seats"
                  value={flightData.availableSeats}
                  onChange={(event) =>
                    setFlightData({ ...flightData, availableSeats: event.target.value })
                  }
                  required
                />

                <input
                  type="number"
                  placeholder="Base Fare"
                  value={flightData.baseFare}
                  onChange={(event) =>
                    setFlightData({ ...flightData, baseFare: event.target.value })
                  }
                  required
                />

                <button type="submit">Add Flight</button>
              </form>
            </section>
          )}

          <section className="card">
            <h2>Search Flights</h2>

            <form onSubmit={searchFlights} className="grid four">
              <input
                type="text"
                placeholder="Source"
                value={searchData.source}
                onChange={(event) =>
                  setSearchData({ ...searchData, source: event.target.value })
                }
                required
              />

              <input
                type="text"
                placeholder="Destination"
                value={searchData.destination}
                onChange={(event) =>
                  setSearchData({ ...searchData, destination: event.target.value })
                }
                required
              />

              <input
                type="date"
                value={searchData.date}
                onChange={(event) =>
                  setSearchData({ ...searchData, date: event.target.value })
                }
                required
              />

              <button type="submit">Search</button>
            </form>

            {role === "ADMIN" && (
              <button className="secondary" onClick={getAllFlights}>
                Show All Flights
              </button>
            )}
          </section>

          <section className="card">
            <h2>Book Flight</h2>

            <form onSubmit={bookFlight} className="booking-box">
              <div className="grid three">
                <input
                  type="number"
                  placeholder="User ID"
                  value={bookingData.userId}
                  onChange={(event) =>
                    setBookingData({ ...bookingData, userId: event.target.value })
                  }
                  required
                />

                <input
                  type="number"
                  placeholder="Flight ID"
                  value={bookingData.flightId}
                  onChange={(event) =>
                    setBookingData({ ...bookingData, flightId: event.target.value })
                  }
                  required
                />

                <input
                  type="number"
                  placeholder="Number of Tickets"
                  value={bookingData.numberOfTickets}
                  onChange={(event) => updateNumberOfTickets(event.target.value)}
                  min="1"
                  required
                />
              </div>

              <h3>Passenger Details</h3>

              {bookingData.passengers.map((passenger, index) => (
                <div className="grid three passenger-row" key={index}>
                  <input
                    type="text"
                    placeholder={`Passenger ${index + 1} Name`}
                    value={passenger.name}
                    onChange={(event) =>
                      updatePassenger(index, "name", event.target.value)
                    }
                    required
                  />

                  <input
                    type="number"
                    placeholder="Age"
                    value={passenger.age}
                    onChange={(event) =>
                      updatePassenger(index, "age", event.target.value)
                    }
                    required
                  />

                  <input
                    type="text"
                    placeholder="Seat Number"
                    value={passenger.seatNumber}
                    onChange={(event) =>
                      updatePassenger(index, "seatNumber", event.target.value)
                    }
                    required
                  />
                </div>
              ))}

              <button type="submit">Book Ticket</button>
            </form>
          </section>

          <section className="card">
            <div className="table-header">
              <h2>Flights</h2>
              <span>{flights.length} result(s)</span>
            </div>

            <div className="table-wrapper">
              <table>
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Flight No.</th>
                    <th>Source</th>
                    <th>Destination</th>
                    <th>Departure</th>
                    <th>Arrival</th>
                    <th>Total</th>
                    <th>Available</th>
                    <th>Fare</th>
                    <th>Action</th>
                  </tr>
                </thead>

                <tbody>
                  {flights.length === 0 ? (
                    <tr>
                      <td colSpan="10">No flights found</td>
                    </tr>
                  ) : (
                    flights.map((flight) => (
                      <tr key={flight.id}>
                        <td>{flight.id}</td>
                        <td>{flight.flightNumber}</td>
                        <td>{flight.source}</td>
                        <td>{flight.destination}</td>
                        <td>{flight.departureTime}</td>
                        <td>{flight.arrivalTime}</td>
                        <td>{flight.totalSeats}</td>
                        <td>
                          <strong>{flight.availableSeats}</strong>
                        </td>
                        <td>{flight.baseFare}</td>
                        <td>
                          <button
                            className="small"
                            onClick={() => fillBookingFlight(flight.id)}
                          >
                            Select
                          </button>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </section>
        </>
      )}
    </div>
  );
}

export default App;