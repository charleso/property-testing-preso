import           Control.Monad

import qualified Test.Aeson

import           System.Exit
import           System.IO


main :: IO ()
main =
  hSetBuffering stdout LineBuffering >> mapM id [
      Test.Aeson.tests
    ] >>= \rs -> when (not . all id $ rs) exitFailure
