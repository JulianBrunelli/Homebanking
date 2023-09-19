const { createApp } = Vue;

createApp({
    data() {
        return {
            loans: [],
            selectLoan: "",
            selectImage: "../images/panas.jpg",
            originAccounts: [],
            selectOriginAccount: "",
            selectPayment: "",
            amount: null,
            payments: [],
            finalAmount: 0,
        };
    },
    created() {
        this.loadLoans()
        this.loadData()
    },
    methods: {
        loadLoans() {
            axios.get('/api/loans')
                .then(response => {
                    this.loans = response.data
                }).catch(error => console.error(error))
        },
        loadData() {
            axios.get('/api/clients/current')
                .then(response => {
                    this.originAccounts = response.data.accounts
                }).catch(error => console.error(error))
        },
        addLoan() {
            this.payments = this.selectLoan.payments
            this.calculoInteres()
            Swal.fire({
                title: 'Confirm loan request',
                html: `<p class="alertLoan">Loan:  ${this.selectLoan.name}</p>
                    <p class="alertLoan">Amount:  ${this.amount}</p>
                    <p class="alertLoan">Payments:  ${this.selectPayment}</p>
                    <p class="alertLoan">Amount payable:  ${this.finalAmount}</p>
                    <p class="alertLoan">Origin account:  ${this.selectOriginAccount}</p>`,
                showDenyButton: true,
                confirmButtonText: 'Confirm',
                denyButtonText: 'Cancel',
            })
                .then((result) => {
                    if (result.isConfirmed) {
                        let object = {
                            "id": this.selectLoan.id,
                            "amount": this.amount,
                            "payments": this.selectPayment,
                            "numberAccountDestination": this.selectOriginAccount
                        }
                        axios.post("/api/loans", object)
                            .then(response => {
                                Swal.fire('Saved!', '', 'success')
                                    .then(response => {
                                        location.href = '../pages/accounts.html'
                                    })
                            })
                            .catch((error) => {
                                Swal.fire({
                                    icon: 'error',
                                    title: 'Oops...',
                                    text: error.response.data,
                                })
                            })
                    } else {
                        Swal.fire('Changes are not saved', '', 'info')
                    }
                }).catch((error) => console.error(error))
        },
        signOut() {
            axios.post('/api/logout')
                .then(response => {
                    location.href = "../pages/index.html"
                })
                .catch((error) => console.error(error.message));
        },
        calculoInteres() {
            if (this.selectPayment == 6) {
                this.finalAmount = this.amount + (this.amount * 0.075)
                return this.finalAmount;
            }
            else if (this.selectPayment == 12) {
                this.finalAmount = this.amount + (this.amount * 0.105)
                return this.finalAmount;
            }
            else if (this.selectPayment == 24) {
                this.finalAmount = this.amount + (this.amount * 0.165)
                return this.finalAmount;
            }
            else if (this.selectPayment == 36) {
                this.finalAmount = this.amount + (this.amount * 0.225)
                return this.finalAmount;
            }
            else if (this.selectPayment == 48) {
                this.finalAmount = this.amount + (this.amount * 0.335)
                return this.finalAmount;
            }
            else if (this.selectPayment == 60) {
                this.finalAmount = this.amount + (this.amount * 0.395)
                return this.finalAmount;
            } else { return 0 };
        }
    },
    computed: {
        makeover() {
            switch (this.selectLoan.name) {
                case 'Mortgage':
                    this.selectImage = "../images/loan-mortage.jpg"
                    break;
                case 'Car':
                    this.selectImage = "../images/loan-car.jpg"
                    break;
                case 'Companies':
                    this.selectImage = "../images/loan-companies.jpg"
                    break;
                default: this.selectImage = "../images/panas.jpg"
                    break;
            }
        },
    }
}).mount("#app");