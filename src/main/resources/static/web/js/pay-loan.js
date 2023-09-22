const { createApp } = Vue;

createApp({
    data() {
        return {
            loans: [],
            selectLoan: {},
            originAccounts: [],
            selectOriginAccount: "",
            amount: 0,
        };
    },
    created() {
        this.loadData()
    },
    methods: {
        loadData() {
            axios.get('/api/clients/current')
                .then(response => {
                    this.originAccounts = response.data.accounts.filter(account => account.balance > 0)
                    this.loans = response.data.loans
                }).catch(error => console.error(error))
        },
        payLoan() {
            let finalAmount = parseFloat(this.amount.toFixed(2));
            Swal.fire({
                title: 'Do you want to pay a fee?',
                showDenyButton: true,
                confirmButtonText: 'Carry out',
                denyButtonText: 'Cancel',
            })
                .then((result) => {
                    if (result.isConfirmed) {
                        axios.patch("/api/loans/current/pay", `idClientLoan=${this.selectLoan.id}&idAccount=${this.selectOriginAccount}&amount=${finalAmount}`)
                            .then(response => {
                                Swal.fire('A fee has been paid!', '', 'success').then(response => {
                                    location.href = './accounts.html'
                                })
                            })
                            .catch((error) => {
                                Swal.fire({
                                    icon: 'error',
                                    title: 'Please try again later...',
                                    text: error.response.data || 'Something went wrong',
                                })
                            })
                    } else {
                        Swal.fire('The transaction was not carried out', '', 'info')
                    }
                })
        },
        signOut() {
            axios.post('/api/logout')
                .then(response => {
                    location.href = "../pages/index.html"
                })
                .catch((error) => console.error(error.message));
        },
    },
    computed: {
        finalAmount() {
            return this.amount = (this.selectLoan.amount / this.selectLoan.payments) || 0
        }
    }
}).mount("#app");