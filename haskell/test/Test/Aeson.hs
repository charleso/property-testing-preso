{-# LANGUAGE TemplateHaskell #-}
module Test.Aeson where

import           Control.Applicative

import           Data.Aeson
import           Data.Aeson.Types
import           Data.Time

import           Test.QuickCheck
import           Test.QuickCheck.Instances ()


toFromJSON :: (ToJSON a, FromJSON a, Eq a, Show a) => a -> Property
toFromJSON a =
  parseEither (parseJSON . toJSON) a === Right a

prop_time :: UTCTime -> Property
prop_time t = toFromJSON t


return []
tests :: IO Bool
tests = $quickCheckAll
