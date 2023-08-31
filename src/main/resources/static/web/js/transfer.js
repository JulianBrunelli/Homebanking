const { createApp } = Vue;

createApp({
    data() {
        return {
            sourceAccount: [],
            destinationAccount: "",
            originAccount: "",
            description: "",
            amount: 0,
            destination: null,
            loader: true,
        };
    },
    created() {
        this.loadData()
    },
    methods: {
        loadData() {
            axios.get("/api/clients/current")
                .then(response => {
                    this.sourceAccount = response.data.accounts.sort((a, b) => b.balance - a.balance)
                    this.numberAccounts = this.sourceAccount.map(account => account.number)
                    this.loader = false
                })
        },
        addTransaction() {
            Swal.fire({
                title: 'Are you sure you want to make the transaction?',
                showDenyButton: true,
                confirmButtonText: 'Carry out',
                denyButtonText: 'Cancel',
            })
                .then((result) => {
                    if (result.isConfirmed) {
                        axios.post("/api/transactions", `amount=${this.amount}&description=${this.description}&originAccountNumber=${this.originAccount}&destinationAccountNumber=${this.destinationAccount}`)
                            .then(response => {
                                Swal.fire('Transaction made!', '', 'success').then(response => {
                                    location.href = './accounts.html'
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
}).mount("#app");
