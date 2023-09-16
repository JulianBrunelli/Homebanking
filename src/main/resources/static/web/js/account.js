const { createApp } = Vue;

createApp({
    data() {
        return {
            accounts: [],
            accountBalance: "",
            transactions: [],
            loader: true,
            date: "",
            time: "",
            idParams: "",
        };
    },
    created() {
        this.loadData()
    },
    methods: {
        loadData() {
            const parameter = location.search
            const params = new URLSearchParams(parameter)
            this.idParams = params.get("id")
            axios.get(`/api/clients/accounts/${this.idParams}`)
                .then(response => {
                    this.accounts = response.data
                    this.accountBalance = this.accounts.balance.toLocaleString()
                    this.transactions = this.accounts.transactions.sort((a, b) => b.id - a.id)
                    this.date = this.transactions.map(transaction => transaction.date.slice(0, 10).replace(/-/g, '/'))
                    this.time = this.transactions.map(transiction => transiction.date.slice(14, -7))
                    this.loader = false
                })
                .catch((error) => location.href = "https://es.memedroid.com/memes/detail/2712377/Use-it-for-interesting-things");
        },
        deactiveAccount() {
            Swal.fire({
                title: 'Are you sure you want to delete the account?',
                showDenyButton: true,
                confirmButtonText: 'Confirm',
                denyButtonText: 'Cancel',
            }).then((result) => {
                if (result.isConfirmed) {
                    axios.patch("/api/clients/current/accounts/deactivate", `id=${this.idParams}`)
                        .then(response => {
                            Swal.fire('Deleted account!', '', 'success')
                                .then(response => {
                                    location.href = '../pages/accounts.html'
                                })
                        })
                        .catch((error) => {
                            Swal.fire({
                                icon: 'error',
                                title: 'Oops...',
                                text: error.response.data,
                            }).then(response => {
                                location.href = '../pages/transfer.html'
                            })
                                .catch(error => console.error(error))
                        })
                } else {
                    Swal.fire('Changes are not saved', '', 'info')
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