const { createApp } = Vue;

createApp({
    data() {
        return {
            firstName: "",
            lastName: "",
            email: "",
            password: "",
        };
    },
    created() {
    },
    methods: {
        checkLogIn(event) {
            event.preventDefault();
            if (this.email && this.password) {
                this.logIn()
            } else {
                Swal.fire({
                    icon: 'error',
                    title: 'Oops...',
                    text: error.response.data,
                })
            }
        },
        logIn() {
            axios.post('/api/login', `email=${this.email}&password=${this.password}`,
                { headers: { 'content-type': 'application/x-www-form-urlencoded' } })
                .then(response => {
                    if (this.email == "julianbrunelli@outlook.com") {
                        location.href = "../../admin/manager.html"
                    } else {
                        location.href = "./accounts.html"
                    }
                })
                .catch((error) => {
                    Swal.fire({
                        icon: 'error',
                        title: 'Oops...',
                        text: error.response.data,
                    })
                })
        },
        signOut() {
            axios.post('/api/logout')
                .then(response => {
                    location.href = "../pages/index.html"
                })
                .catch((error) => console.error(error.message));
        },
        checkSingUp(event) {
            event.preventDefault();
            if (this.firstName && this.lastName && this.email && this.password) {
                this.addClient()
            } else {
                if (this.firstName == "") {
                    alert("Please provide a first name")
                }
                if (this.lastName == "") {
                    alert("Please provide a last name")
                }
                if (this.email == "") {
                    alert("Please provide an email")
                }
                if (this.password == "") {
                    alert("Please provide a password")
                }
            }
        },
        addClient() {
            axios.post('/api/clients', `firstName=${this.firstName}&lastName=${this.lastName}&email=${this.email}&password=${this.password}`,
                { headers: { 'content-type': 'application/x-www-form-urlencoded' } })
                .then(response => {
                    this.logIn()
                })
                .catch((error) => alert(error.response.data));
        },
    },
}).mount("#app");