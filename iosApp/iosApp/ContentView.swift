import SwiftUI
import SharedLogic

struct ContentView: View {
    let coin = SharedLogic.shared.sampleCoin()

    var body: some View {
        VStack(spacing: 8) {
            Text(coin.symbol)
            Text(coin.price.amountRaw)
            Text("\(coin.change24h)")
        }
    }}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
