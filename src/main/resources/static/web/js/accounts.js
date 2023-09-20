const { createApp } = Vue;

createApp({
    data() {
        return {
            nameClient: "",
            clientsAccounts: [],
            originAccount: "",
            clientLoans: [],
            loans: [],
            accountType: "",
            loader: true,
            loanId: null,
        };
    },
    created() {
        this.loadData()
    },
    methods: {
        loadData() {
            axios.get("/api/clients/current")// { headers: { 'accept': 'application/xml' } }
                .then(response => {
                    this.nameClient = response.data.firstName + " " + response.data.lastName
                    this.clientsAccounts = response.data.accounts.sort((a, b) => a.id - b.id).filter(account => account.active)
                    this.loans = response.data.loans
                    this.loader = false
                })
                .catch((error) => console.error(error.message));
        },
        addAccount() {
            Swal.fire({
                title: 'Are you sure you want to create an account?',
                showDenyButton: true,
                confirmButtonText: 'Create',
                denyButtonText: 'Cancel',
            })
                .then((result) => {
                    if (result.isConfirmed) {
                        Swal.fire({
                            title: 'Please select an account type',
                            html: `<section class="container-type">
                            <h2 class="mb-4 w-100">Account type</h2>
                            <div class="form-check">
                                <input class="form-check-input" type="radio" name="accountType" id="SAVING"
                                    value="SAVING">
                                <label class="form-check-label mb-2" for="SAVING">
                                    SAVING
                                </label>
                            </div>
                            <div class="form-check">
                                <input class="form-check-input" type="radio" name="accountType" id="CURRENT"
                                    value="CURRENT">
                                <label class="form-check-label" for="CURRENT">
                                    CURRENT
                                </label>
                            </div>
                        </section>`
                        })
                            .then((result) => {
                                const selected = document.querySelector("input[name=accountType]:checked")
                                axios.post("/api/clients/current/accounts", `type=${selected.value}`)
                                    .then(response => {
                                        Swal.fire('Account saved!', '', 'success').then(response => {
                                            location.href = '../pages/accounts.html'
                                        })
                                    })
                            }).catch((error) => error.response.data)
                    } else {
                        Swal.fire('Your account was not saved', '', 'info')
                    }
                }).catch((error) => error.response.data)
        },
        signOut() {
            axios.post('/api/logout')
                .then(response => {
                    location.href = "../pages/index.html"
                })
                .catch((error) => console.error(error.message));
        },
    },
}).mount("#app");
